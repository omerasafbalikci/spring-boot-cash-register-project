package com.toyota.salesservice.service.concretes;

import com.toyota.salesservice.dao.SalesRepository;
import com.toyota.salesservice.domain.Sales;
import com.toyota.salesservice.domain.SalesItems;
import com.toyota.salesservice.dto.requests.CreateSalesItemsRequest;
import com.toyota.salesservice.dto.requests.CreateSalesRequest;
import com.toyota.salesservice.dto.requests.InventoryRequest;
import com.toyota.salesservice.dto.responses.GetAllSalesResponse;
import com.toyota.salesservice.dto.responses.InventoryResponse;
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

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class SalesManager implements SalesService {
    private final SalesRepository salesRepository;
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

        List<Long> campaignIds = createSalesRequest.getCreateSalesItemsRequests().stream()
                .map(CreateSalesItemsRequest::getCampaignId).toList();

        List<InventoryRequest> inventoryRequests = createSalesRequest.getCreateSalesItemsRequests().stream()
                .map(salesItem -> this.modelMapperService.forRequest().map(salesItem, InventoryRequest.class))
                .toList();

        List<InventoryResponse> inventoryResponses = webClientBuilder.build().post()
                .uri("http://product-service/api/products/checkproductininventory")
                .body(BodyInserters.fromValue(inventoryRequests))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<InventoryResponse>>() {})
                .block();

        if (inventoryResponses != null && !inventoryResponses.isEmpty()) {
            List<SalesItems> salesItems = inventoryResponses.stream()
                    .map(response -> {
                        SalesItems salesItem = new SalesItems();
                        salesItem.setSales(sales);
                        salesItem.setBarcodeNumber(response.getBarcodeNumber());
                        salesItem.setSkuCode(response.getSkuCode());
                        salesItem.setName(response.getName());
                        salesItem.setQuantity(response.getQuantity());
                        salesItem.setUnitPrice(response.getUnitPrice());
                        salesItem.setState(response.getState());
                        salesItem.getCampaign().setId((campaignIds.get(inventoryResponses.indexOf(response))));
                        return salesItem;
                    })
                    .toList();
            sales.setSalesItemsList(salesItems);

            for (SalesItems salesItem : salesItems) {
                if (!salesItem.getState()) {
                    throw new ProductStatusFalseException(salesItem.getName() + " status is false");
                }

                if (salesItem.getCampaign() != null) {
                    if (salesItem.getCampaign().getCampaignType() == 1) {
                        double price = salesItem.getUnitPrice();
                        double sets = price / (salesItem.getCampaign().getBuyPayPartOne() + salesItem.getCampaign().getBuyPayPartTwo());
                        Double newPrice = price - (sets * salesItem.getCampaign().getBuyPayPartTwo());
                        salesItem.setUnitPrice(newPrice);
                    } else if (salesItem.getCampaign().getCampaignType() == 2) {
                        double price = salesItem.getUnitPrice();
                        Double newPrice = price - (price * salesItem.getCampaign().getPercent() / 100);
                        salesItem.setUnitPrice(newPrice);
                    } else if (salesItem.getCampaign().getCampaignType() == 3) {
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
                sales.setMoney(createSalesRequest.getMoney());
            } else if (createSalesRequest.getPaymentType().equals("k") || createSalesRequest.getPaymentType().equals("K")) {
                sales.setMoney(totalPrice);
            } else {
                throw new PaymentTypeIncorrectEntryException("Payment type is ıncorrect entry");
            }

            if (sales.getMoney() >= totalPrice) {
                sales.setChange(sales.getMoney() - sales.getTotalPrice());
                this.salesRepository.save(sales);
                return this.modelMapperService.forResponse().map(sales, GetAllSalesResponse.class);
            } else {
                webClientBuilder.build().post()
                        .uri("http://product-service/api/products/updateproductininventory")
                        .body(BodyInserters.fromValue(inventoryRequests))
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<InventoryResponse>>() {})
                        .block();
                throw new InsufficientBalanceException("Insufficient balance");
            }
        } else {
            throw new FetchInventoryResponseException("Error while fetching inventory response");
        }
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
    public TreeMap<String, Object> getAllSalesPage(int page, int size, String[] sort) {
        logger.info("Fetching all sales with pagination. Page: {}, Size: {}, Sort: {}.", page, size, Arrays.toString(sort));
        Pageable pagingSort = PageRequest.of(page, size, Sort.by(getOrder(sort)));
        Page<Sales> pagePro = this.salesRepository.findAll(pagingSort);

        List<GetAllSalesResponse> responses = pagePro.getContent().stream()
                .map(product -> this.modelMapperService.forResponse()
                        .map(product, GetAllSalesResponse.class)).collect(Collectors.toList());

        TreeMap<String, Object> response = new TreeMap<>();
        response.put("sales", responses);
        response.put("currentPage", pagePro.getNumber());
        response.put("totalItems", pagePro.getTotalElements());
        response.put("totalPages", pagePro.getTotalPages());
        logger.debug("Retrieved {} sales for page {}. Total items: {}. Total pages: {}.", responses.size(), pagePro.getNumber(), pagePro.getTotalElements(), pagePro.getTotalPages());
        return response;
    }

    @Override
    public GetAllSalesResponse getSalesBySalesNumber(String salesNumber) {
        logger.info("Fetching sales by sales number '{}'.", salesNumber);
        Sales sales = this.salesRepository.findBySalesNumber(salesNumber);
        if (sales != null) {
            logger.debug("Sales found with sales number '{}'.", salesNumber);
            return this.modelMapperService.forResponse().map(sales, GetAllSalesResponse.class);
        } else {
            logger.warn("No sales found with sales number '{}'.", salesNumber);
            throw new SalesNotFoundException("Sales not found");
        }
    }

    @Override
    public GetAllSalesResponse getProductById(Long id) {
        logger.info("Fetching sales by id '{}'.", id);
        Sales sales = this.salesRepository.findById(id).orElseThrow(() -> {
            logger.warn("No sales found with id '{}'.", id);
            return new SalesNotFoundException("Sales not found");
        });
        logger.debug("Sales found with id '{}'.", id);
        return this.modelMapperService.forResponse().map(sales, GetAllSalesResponse.class);
    }
}
