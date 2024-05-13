package com.toyota.salesservice.service.concretes;

import com.toyota.salesservice.dao.CampaignRepository;
import com.toyota.salesservice.dao.SalesRepository;
import com.toyota.salesservice.domain.Campaign;
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
                .map(request -> Optional.ofNullable(request.getCampaignId()))
                .toList();

        List<InventoryRequest> inventoryRequests = createSalesRequest.getCreateSalesItemsRequests().stream()
                .map(salesItem -> this.modelMapperService.forRequest().map(salesItem, InventoryRequest.class))
                .toList();

        List<InventoryResponse> inventoryResponses = this.webClientBuilder.build().post()
                .uri("http://product-service/api/products/check-product-in-inventory")
                .body(BodyInserters.fromValue(inventoryRequests))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<InventoryResponse>>() {})
                .block();

        if (inventoryResponses != null && !inventoryResponses.isEmpty()) {
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

                        return salesItem;
                    })
                    .collect(Collectors.toList());
            sales.setSalesItemsList(salesItems);

            for (SalesItems salesItem : salesItems) {
                if (!salesItem.getState()) {
                    throw new ProductStatusFalseException(salesItem.getName() + " status is false");
                }

                Campaign campaign = salesItem.getCampaign();
                if (campaign != null) {
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

            if (createSalesRequest.getPaymentType().equals("n") || createSalesRequest.getPaymentType().equals("N")) {
                sales.setPaymentType("Nakit");
                if (createSalesRequest.getMoney() != null) {
                    sales.setMoney(createSalesRequest.getMoney());
                } else {
                    this.salesBusinessRules.updateInventory(inventoryRequests);
                    throw new NoMoneyEnteredException("No money entered");
                }
            } else if (createSalesRequest.getPaymentType().equals("k") || createSalesRequest.getPaymentType().equals("K")) {
                sales.setPaymentType("Kart");
                sales.setMoney(totalPrice);
            } else {
                this.salesBusinessRules.updateInventory(inventoryRequests);
                throw new PaymentTypeIncorrectEntryException("Payment type is ıncorrect entry");
            }

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
        InventoryRequest inventoryRequest = new InventoryRequest();
        inventoryRequest.setBarcodeNumber(createReturnRequest.getBarcodeNumber());
        SalesItems salesItems = new SalesItems();
        if (sales != null) {
            LocalDateTime salesDate = sales.getSalesDate();
            LocalDateTime returnDate = createReturnRequest.getReturnDate();
            boolean hasBarcodeNumber = false;

            Duration duration = Duration.between(salesDate, returnDate);
            long days = duration.toDays();
            if (days > 15) {
                throw new ReturnPeriodExpiredException("Return period has expired");
            } else {
                for (SalesItems salesItem : sales.getSalesItemsList()) {
                    if (salesItem.getBarcodeNumber().equals(createReturnRequest.getBarcodeNumber())) {
                        salesItems = salesItem;
                        salesItem.setDeleted(true);
                        hasBarcodeNumber = true;
                        if (salesItem.getQuantity() < createReturnRequest.getQuantity()) {
                            throw new QuantityIncorrectEntryException("Quantity incorrect entry");
                        }
                        inventoryRequest.setQuantity(createReturnRequest.getQuantity());
                        this.webClientBuilder.build().post()
                                .uri("http://product-service/api/products/returned-product")
                                .body(BodyInserters.fromValue(inventoryRequest))
                                .retrieve()
                                .toBodilessEntity()
                                .block();
                        this.salesRepository.save(sales);
                    }
                }
                if (!hasBarcodeNumber) {
                    throw new SalesItemsNotFoundException("Sales items not found");
                }
            }
        } else {
            throw new SalesNotFoundException("Sales not found");
        }
        return this.modelMapperService.forResponse().map(salesItems, GetAllSalesItemsResponse.class);
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
