package com.toyota.salesservice.service.concretes;

import com.toyota.salesservice.dao.CampaignRepository;
import com.toyota.salesservice.dao.SalesRepository;
import com.toyota.salesservice.domain.Campaign;
import com.toyota.salesservice.domain.PaymentType;
import com.toyota.salesservice.domain.Sales;
import com.toyota.salesservice.domain.SalesItems;
import com.toyota.salesservice.dto.requests.CreateReturnRequest;
import com.toyota.salesservice.dto.requests.CreateSalesItemsRequest;
import com.toyota.salesservice.dto.requests.CreateSalesRequest;
import com.toyota.salesservice.dto.requests.InventoryRequest;
import com.toyota.salesservice.dto.responses.GetAllSalesItemsResponse;
import com.toyota.salesservice.dto.responses.GetAllSalesResponse;
import com.toyota.salesservice.dto.responses.InventoryResponse;
import com.toyota.salesservice.dto.responses.PaginationResponse;
import com.toyota.salesservice.service.abstracts.SalesService;
import com.toyota.salesservice.service.rules.SalesBusinessRules;
import com.toyota.salesservice.utilities.exceptions.*;
import com.toyota.salesservice.utilities.mappers.ModelMapperService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
@AllArgsConstructor
public class SalesManager implements SalesService {
    private final SalesRepository salesRepository;
    private final CampaignRepository campaignRepository;
    private final Logger logger = LogManager.getLogger(SalesService.class);
    private final WebClient.Builder webClientBuilder;
    private final ModelMapperService modelMapperService;
    private final SalesBusinessRules salesBusinessRules;

    @Override
    public GetAllSalesResponse addSales(CreateSalesRequest createSalesRequest) {
        Sales sales = new Sales();
        sales.setSalesNumber(UUID.randomUUID().toString().substring(0, 8));
        sales.setSalesDate(LocalDateTime.now());
        sales.setCreatedBy(createSalesRequest.getCreatedBy());

        List<String> barcodeNumbers = createSalesRequest.getCreateSalesItemsRequests().stream()
                .map(CreateSalesItemsRequest::getBarcodeNumber).toList();

        List<Optional<Long>> campaignIds = createSalesRequest.getCreateSalesItemsRequests().stream()
                .map(request -> Optional.ofNullable(request.getCampaignId())).toList();

        List<Optional<String>> paymentTypes = createSalesRequest.getCreateSalesItemsRequests().stream()
                .map(request -> Optional.ofNullable(request.getPaymentType())).toList();

        if ((createSalesRequest.getPaymentType() == null) && (paymentTypes.stream().allMatch(Optional::isEmpty) || paymentTypes.stream().anyMatch(Optional::isEmpty))) {
            throw new PaymentTypeNotEnteredException("Payment type not entered");
        }

        if (createSalesRequest.getPaymentType() != null) {
            PaymentType paymentType = PaymentType.valueOf(createSalesRequest.getPaymentType().toUpperCase());
            sales.setPaymentType(paymentType);
        }

        List<InventoryRequest> inventoryRequests = createSalesRequest.getCreateSalesItemsRequests().stream()
                .map(salesItem -> this.modelMapperService.forRequest().map(salesItem, InventoryRequest.class)).toList();

        Mono<List<InventoryResponse>> inventoryResponseMono = this.webClientBuilder.build().post()
                .uri("http://product-service/api/products/check-product-in-inventory")
                .body(BodyInserters.fromValue(inventoryRequests))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<InventoryResponse>>() {});

        List<InventoryResponse> inventoryResponses = inventoryResponseMono.blockOptional().orElse(Collections.emptyList());


        if (!inventoryResponses.isEmpty()) {
            List<SalesItems> salesItems = IntStream.range(0, inventoryResponses.size())
                    .mapToObj(i -> {
                        InventoryResponse response = inventoryResponses.get(i);
                        SalesItems salesItem = new SalesItems();
                        salesItem.setBarcodeNumber(barcodeNumbers.get(i));
                        salesItem.setSales(sales);
                        salesItem.setName(response.getName());
                        salesItem.setQuantity(response.getQuantity());
                        salesItem.setUnitPrice(response.getUnitPrice());
                        salesItem.setState(response.getState());

                        Optional<Long> campaignIdOptional = campaignIds.get(i);
                        campaignIdOptional.ifPresent(campaignId -> {
                            Optional<Campaign> optionalCampaign = this.campaignRepository.findById(campaignId);
                            optionalCampaign.ifPresent(salesItem::setCampaign);
                        });

                        Optional<String> paymentTypeOptional = paymentTypes.get(i);
                        paymentTypeOptional.ifPresent(paymentType -> {
                            PaymentType type = PaymentType.valueOf(paymentType.toUpperCase());
                            salesItem.setPaymentType(type);
                        });

                        return salesItem;
                    })
                    .collect(Collectors.toList());
            sales.setSalesItemsList(salesItems);

            for (SalesItems salesItem : salesItems) {
                if (salesItem.getPaymentType() == null) {
                    salesItem.setPaymentType(sales.getPaymentType());
                }

                if (!salesItem.getState()) {
                    this.salesBusinessRules.updateInventory(inventoryRequests);
                    throw new ProductStatusFalseException(salesItem.getName() + " status is false");
                }

                Campaign campaign = salesItem.getCampaign();
                if (campaign != null && campaign.getState()) {
                    Integer campaignType = campaign.getCampaignType();
                    if (campaignType == 1 && salesItem.getQuantity() >= salesItem.getCampaign().getBuyPayPartOne()) {
                        double price = salesItem.getUnitPrice();
                        double setPrice = price / (salesItem.getCampaign().getBuyPayPartOne() + salesItem.getCampaign().getBuyPayPartTwo());
                        int totalSets = salesItem.getQuantity() / (salesItem.getCampaign().getBuyPayPartOne() + salesItem.getCampaign().getBuyPayPartTwo());
                        Double newPrice = price * totalSets - (setPrice * salesItem.getCampaign().getBuyPayPartTwo() * totalSets);
                        salesItem.setUnitPrice(newPrice);
                    } else if (campaignType == 2) {
                        double price = salesItem.getUnitPrice();
                        Double newPrice = price - (price * salesItem.getCampaign().getPercent() / 100);
                        salesItem.setUnitPrice(newPrice);
                    } else if (campaignType == 3) {
                        double price = salesItem.getUnitPrice();
                        Double newPrice = price - salesItem.getCampaign().getMoneyDiscount();
                        salesItem.setUnitPrice(newPrice);
                    }
                }
            }

            double totalPrice = salesItems.stream()
                    .mapToDouble(salesItem -> salesItem.getUnitPrice() * salesItem.getQuantity())
                    .sum();

            sales.setTotalPrice(totalPrice);

            Double money = 0.0;
            boolean isCash = false;
            for (SalesItems salesItem : salesItems) {
                if (salesItem.getPaymentType() == PaymentType.CARD) {
                    money += (salesItem.getUnitPrice() * salesItem.getQuantity());
                } else if (salesItem.getPaymentType() == PaymentType.CASH) {
                    isCash = true;
                }
            }
            if (isCash) {
                if (createSalesRequest.getMoney() == null) {
                    this.salesBusinessRules.updateInventory(inventoryRequests);
                    throw new NoMoneyEnteredException("No money entered");
                }
                money += createSalesRequest.getMoney();
            }

            sales.setMoney(money);

            if (sales.getMoney() >= totalPrice) {
                sales.setChange(sales.getMoney() - sales.getTotalPrice());
                this.salesRepository.save(sales);
                return this.modelMapperService.forResponse().map(sales, GetAllSalesResponse.class);
            } else {
                this.salesBusinessRules.updateInventory(inventoryRequests);
                throw new InsufficientBalanceException("Insufficient balance");
            }
        } else {
            throw new FetchInventoryResponseException("Error while fetching inventory response");
        }
    }

    @Override
    public GetAllSalesItemsResponse toReturn(CreateReturnRequest createReturnRequest) {
        Sales sales = this.salesRepository.findBySalesNumber(createReturnRequest.getSalesNumber());

        if (sales == null) {
            throw new SalesNotFoundException("Sales not found");
        }

        LocalDateTime salesDate = sales.getSalesDate();
        LocalDateTime returnDate = createReturnRequest.getReturnDate();
        Duration duration = Duration.between(salesDate, returnDate);
        long days = duration.toDays();

        if (days > 15) {
            throw new ReturnPeriodExpiredException("Return period has expired");
        }

        SalesItems salesItem = null;
        boolean hasBarcodeNumber = false;

        for (SalesItems item : sales.getSalesItemsList()) {
            if (item.getBarcodeNumber().equals(createReturnRequest.getBarcodeNumber())) {
                salesItem = item;
                hasBarcodeNumber = true;
                break;
            }
        }

        if (!hasBarcodeNumber) {
            throw new SalesItemsNotFoundException("Sales items not found");
        }

        int returnQuantity = createReturnRequest.getQuantity();
        if (salesItem.getQuantity() < returnQuantity) {
            throw new QuantityIncorrectEntryException("Quantity incorrect entry");
        }

        InventoryRequest inventoryRequest = new InventoryRequest();
        inventoryRequest.setBarcodeNumber(createReturnRequest.getBarcodeNumber());
        inventoryRequest.setQuantity(returnQuantity);

        Mono<Void> returnedProductMono = this.webClientBuilder.build().post()
                .uri("http://product-service/api/products/returned-product")
                .body(BodyInserters.fromValue(inventoryRequest))
                .retrieve()
                .toBodilessEntity()
                .then();

        returnedProductMono.subscribe();

        salesItem.setQuantity(salesItem.getQuantity() - returnQuantity);
        sales.setTotalPrice(sales.getTotalPrice() - (salesItem.getUnitPrice() * returnQuantity));
        sales.setChange(sales.getChange() + (salesItem.getUnitPrice() * returnQuantity));

        this.salesRepository.save(sales);

        return this.modelMapperService.forResponse().map(salesItem, GetAllSalesItemsResponse.class);
    }

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }

    private List<Sort.Order> getOrder(String[] sort) {
        List<Sort.Order> orders = new ArrayList<>();
        if (sort[0].contains(",")) {
            for (String sortOrder : sort) {
                String[] _sort = sortOrder.split(",");
                orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
            }
        } else {
            orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
        }
        return orders;
    }

    @Override
    public PaginationResponse<GetAllSalesResponse> getAllSalesPage(int page, int size, String[] sort, Long id, String salesNumber,
                                              String salesDate, String createdBy, String paymentType,
                                              Double totalPrice, Double money, Double change) {
        logger.info("Fetching all sales with pagination. Page: {}, Size: {}, Sort: {}.", page, size, Arrays.toString(sort));
        Pageable pagingSort = PageRequest.of(page, size, Sort.by(getOrder(sort)));
        Page<Sales> pageSales = this.salesRepository.getSalesFiltered(id, salesNumber,
                salesDate, createdBy, paymentType, totalPrice, money, change, pagingSort);

        List<GetAllSalesResponse> responses = pageSales.getContent().stream()
                .map(sales -> this.modelMapperService.forResponse()
                        .map(sales, GetAllSalesResponse.class)).toList();

        return new PaginationResponse<>(responses, pageSales);
    }
}
