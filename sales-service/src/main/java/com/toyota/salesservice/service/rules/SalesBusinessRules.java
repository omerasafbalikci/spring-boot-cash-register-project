package com.toyota.salesservice.service.rules;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toyota.salesservice.dao.CampaignRepository;
import com.toyota.salesservice.domain.Campaign;
import com.toyota.salesservice.domain.PaymentType;
import com.toyota.salesservice.domain.Sales;
import com.toyota.salesservice.domain.SalesItems;
import com.toyota.salesservice.dto.requests.CreateSalesItemsRequest;
import com.toyota.salesservice.dto.requests.CreateSalesRequest;
import com.toyota.salesservice.dto.requests.InventoryRequest;
import com.toyota.salesservice.dto.responses.InventoryResponse;
import com.toyota.salesservice.service.abstracts.SalesService;
import com.toyota.salesservice.utilities.exceptions.*;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Service for handling business rules related to sales.
 */

@Service
@AllArgsConstructor
public class SalesBusinessRules {
    private final CampaignRepository campaignRepository;
    private final Logger logger = LogManager.getLogger(SalesService.class);
    private final WebClient.Builder webClientBuilder;

    /**
     * Validates the payment type for a sales record.
     *
     * @param sales              the sales record
     * @param createSalesRequest the sales request data
     */
    public void validatePaymentType(Sales sales, CreateSalesRequest createSalesRequest) {
        logger.info("Validating payment type for sales record created by '{}'.", createSalesRequest.getCreatedBy());
        List<Optional<PaymentType>> paymentTypes = createSalesRequest.getCreateSalesItemsRequests().stream()
                .map(request -> Optional.ofNullable(request.getPaymentType())).toList();

        if ((createSalesRequest.getPaymentType() == null) && (paymentTypes.stream().anyMatch(Optional::isEmpty))) {
            logger.error("Payment type not entered.");
            throw new PaymentTypeNotEnteredException("Payment type not entered");
        }
        if (createSalesRequest.getPaymentType() != null) {
            sales.setPaymentType(createSalesRequest.getPaymentType());
        }
    }

    /**
     * Sends a web client request to check product inventory.
     *
     * @param inventoryRequests the list of inventory requests
     * @return the list of inventory responses
     */
    public List<InventoryResponse> checkInInventory(List<InventoryRequest> inventoryRequests) {
        logger.info("Sending web client request to check product inventory.");
        Mono<List<InventoryResponse>> inventoryResponseMono = this.webClientBuilder.build().post()
                .uri("http://product-service/api/products/check-product-in-inventory")
                .body(BodyInserters.fromValue(inventoryRequests))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                            logger.error("Client error: {}.", errorBody);
                            if (errorBody.contains("Product is not in stock")) {
                                logger.warn("Product is not in stock for barcode: {}", extractProductBarcodeNumber(errorBody));
                                return Mono.error(new ProductIsNotInStockException("Product is not in stock: " + extractProductBarcodeNumber(errorBody)));
                            } else if (errorBody.contains("Product not found")) {
                                logger.warn("Product not found for barcode: {}", extractProductBarcodeNumber(errorBody));
                                return Mono.error(new ProductNotFoundException("Product not found: " + extractProductBarcodeNumber(errorBody)));
                            } else {
                                logger.warn("Unexpected client error: {}", errorBody);
                                return Mono.error(new UnexpectedException("Unexpected exception in product-service"));
                            }
                        })
                )
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                            logger.error("Server error: {}.", errorBody);
                            return Mono.error(new UnexpectedException("Server error: " + errorBody));
                        })
                )
                .bodyToMono(new ParameterizedTypeReference<>() {});

        List<InventoryResponse> inventoryResponses = inventoryResponseMono.blockOptional().orElse(Collections.emptyList());
        logger.info("Received {} inventory responses.", inventoryResponses.size());
        return inventoryResponses;
    }

    /**
     * Extracts the product barcode number from a JSON error message.
     *
     * @param errorBody the error message body from which to extract the barcode number
     * @return the extracted barcode number or "Unknown Product" if not found
     */
    private String extractProductBarcodeNumber(String errorBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(errorBody);
            String message = rootNode.path("message").asText();
            if (message.contains(":")) {
                return message.substring(message.indexOf(":") + 2).trim();
            }
        } catch (Exception e) {
            logger.error("Error parsing error body JSON: {}", e.getMessage());
        }
        return "Unknown Product";
    }

    /**
     * Creates sales items for a sales record.
     *
     * @param createSalesRequest  the sales request data
     * @param sales               the sales record
     * @param inventoryResponses  the list of inventory responses
     * @return the list of sales items
     */
    public List<SalesItems> createSalesItems(CreateSalesRequest createSalesRequest, Sales sales, List<InventoryResponse> inventoryResponses) {
        logger.info("Create sales items for sales record created by '{}'.", createSalesRequest.getCreatedBy());
        List<String> barcodeNumbers = createSalesRequest.getCreateSalesItemsRequests().stream()
                .map(CreateSalesItemsRequest::getBarcodeNumber).toList();
        List<Optional<Long>> campaignIds = createSalesRequest.getCreateSalesItemsRequests().stream()
                .map(request -> Optional.ofNullable(request.getCampaignId())).toList();
        List<Optional<PaymentType>> paymentTypes = createSalesRequest.getCreateSalesItemsRequests().stream()
                .map(request -> Optional.ofNullable(request.getPaymentType())).toList();

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
                    salesItem.setTotalPrice(response.getUnitPrice() * response.getQuantity());
                    campaignIds.get(i).flatMap(this.campaignRepository::findById).ifPresent(campaign -> {
                        salesItem.setCampaign(campaign);
                        logger.info("Set campaign for sales item with barcode '{}'.", barcodeNumbers.get(i));
                    });
                    paymentTypes.get(i).ifPresent(paymentType -> {
                        salesItem.setPaymentType(paymentType);
                        logger.info("Set payment type '{}' for sales item with barcode '{}'.", paymentType, barcodeNumbers.get(i));
                    });
                    logger.debug("Created sales item: {}.", salesItem);
                    return salesItem;
                })
                .collect(Collectors.toList());
        logger.info("Created {} sales items.", salesItems.size());
        return salesItems;
    }

    /**
     * Updates sales items for a sales record.
     *
     * @param sales             the sales record
     * @param salesItems        the list of sales items
     * @param inventoryRequests the list of inventory requests
     */
    public void updateSalesItems(Sales sales, List<SalesItems> salesItems, List<InventoryRequest> inventoryRequests) {
        logger.info("Updating sales items for sales record with sales number '{}'.", sales.getSalesNumber());
        for (SalesItems salesItem : salesItems) {
            if (salesItem.getPaymentType() == null) {
                salesItem.setPaymentType(sales.getPaymentType());
                logger.info("Set payment type '{}' for sales item with barcode '{}'.", sales.getPaymentType(), salesItem.getBarcodeNumber());
            }
            if (!salesItem.getState()) {
                updateInventory(inventoryRequests);
                logger.error("Product status false for item '{}'.", salesItem.getName());
                throw new ProductStatusFalseException(salesItem.getName() + " status is false");
            }
            Campaign campaign = salesItem.getCampaign();
            if (campaign != null && campaign.getState()) {
                applyCampaignDiscount(salesItem);
                logger.info("Applied campaign discount for sales item with barcode '{}'.", salesItem.getBarcodeNumber());
            } else if (campaign != null) {
                updateInventory(inventoryRequests);
                logger.error("Campaign status false for item '{}'.", salesItem.getName());
                throw new CampaignStateFalseException("Campaign state is false: " + campaign.getName());
            }
            logger.debug("Updated sales item: {}.", salesItem.getName());
        }
        logger.info("Updated all sales items for sales record with sales number '{}'.", sales.getSalesNumber());
    }

    /**
     * Applies campaign discount to a sales item.
     *
     * @param salesItem the sales item
     */
    public void applyCampaignDiscount(SalesItems salesItem) {
        logger.info("Applying campaign discount for item '{}'.", salesItem.getName());
        int campaignType = salesItem.getCampaign().getCampaignType();
        int quantity = salesItem.getQuantity();
        double pricePerUnit = salesItem.getUnitPrice();
        double totalPrice = pricePerUnit * quantity;

        if (campaignType == 1 && quantity >= salesItem.getCampaign().getBuyPayPartOne()) {
            int buyPart = salesItem.getCampaign().getBuyPayPartOne();
            int payPart = salesItem.getCampaign().getBuyPayPartTwo();
            int sets = quantity / buyPart;
            int remainingItems = quantity % buyPart;
            double newTotalPrice = (sets * payPart + remainingItems) * pricePerUnit;
            salesItem.setTotalPrice(newTotalPrice);
            logger.info("Applied 'Buy X Pay Y' discount: buy part = {}, pay part = {}, sets = {}, remaining items = {}, new total price = {}.",
                    buyPart, payPart, sets, remainingItems, newTotalPrice);
        } else if (campaignType == 2) {
            double discountPercent = salesItem.getCampaign().getPercent();
            double discountAmount = totalPrice * discountPercent / 100;
            if (discountAmount <= totalPrice) {
                double newTotalPrice = totalPrice - discountAmount;
                salesItem.setTotalPrice(newTotalPrice);
                logger.info("Applied percentage discount: discount percent = {}, discount amount = {}, new total price = {}.",
                        discountPercent, discountAmount, newTotalPrice);
            } else {
                logger.warn("Discount amount exceeds total price for item '{}'.", salesItem.getName());
            }
        } else if (campaignType == 3) {
            double discountAmount = salesItem.getCampaign().getMoneyDiscount();
            if (discountAmount <= totalPrice) {
                double newTotalPrice = totalPrice - discountAmount;
                salesItem.setTotalPrice(newTotalPrice);
                logger.info("Applied money discount: discount amount = {}, new total price = {}.",
                        discountAmount, newTotalPrice);
            } else {
                logger.warn("Discount amount exceeds total price for item '{}'.", salesItem.getName());
            }
        } else {
            logger.warn("Unknown campaign type '{}' for item '{}'.", campaignType, salesItem.getName());
        }
    }

    /**
     * Calculates the total money for a sales record.
     *
     * @param createSalesRequest  the sales request data
     * @param salesItems          the list of sales items
     * @param inventoryRequests   the list of inventory requests
     * @return the total money
     */
    public double calculateTotalMoney(CreateSalesRequest createSalesRequest, List<SalesItems> salesItems, List<InventoryRequest> inventoryRequests) {
        logger.info("Calculating total money for sales record created by '{}'.", createSalesRequest.getCreatedBy());
        double money = 0.0;
        boolean isCash = false;

        for (SalesItems salesItem : salesItems) {
            if (salesItem.getPaymentType() == PaymentType.CARD) {
                money += salesItem.getTotalPrice();
                logger.debug("Added card payment for item '{}', total price = {}. Current total money = {}.",
                        salesItem.getName(), salesItem.getTotalPrice(), money);
            } else if (salesItem.getPaymentType() == PaymentType.CASH) {
                isCash = true;
                logger.debug("Cash payment detected for item '{}'.", salesItem.getName());
            }
        }
        if (isCash) {
            if (createSalesRequest.getMoney() == null) {
                updateInventory(inventoryRequests);
                logger.error("No money entered for cash payment.");
                throw new NoMoneyEnteredException("No money entered");
            }
            money += createSalesRequest.getMoney();
            logger.debug("Added cash payment amount: {}. Current total money = {}.",
                    createSalesRequest.getMoney(), money);
        }
        logger.info("Total money calculated: {}", money);
        return money;
    }

    /**
     * Validates the return period for a sales record.
     *
     * @param salesDate  the sales date
     * @param returnDate the return date
     */
    public void validateReturnPeriod(LocalDateTime salesDate, LocalDateTime returnDate) {
        logger.info("Validating return period for sales date: {} and return date: {}.", salesDate, returnDate);
        Duration duration = Duration.between(salesDate, returnDate);
        if (duration.toDays() > 15) {
            logger.error("Return period has expired.");
            throw new ReturnPeriodExpiredException("Return period has expired");
        }
    }

    /**
     * Updates the inventory for a list of items.
     *
     * @param inventoryRequests the list of inventory requests
     */
    public void updateInventory(List<InventoryRequest> inventoryRequests) {
        logger.info("Updating inventory for {} items.", inventoryRequests.size());
        Mono<Void> updateInventoryMono = this.webClientBuilder.build().post()
                .uri("http://product-service/api/products/update-product-in-inventory")
                .body(BodyInserters.fromValue(inventoryRequests))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> {
                    logger.error("Error response from product service: {}", clientResponse.statusCode());
                    return clientResponse.createException().flatMap(Mono::error);
                })
                .toBodilessEntity()
                .then();

        updateInventoryMono.subscribe(
                success -> logger.info("Inventory updated successfully."),
                error -> logger.error("Error updating inventory: ", error)
        );
    }
}
