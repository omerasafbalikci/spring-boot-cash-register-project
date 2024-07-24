package com.toyota.salesservice.service.concretes;

import com.toyota.salesservice.dao.SalesItemRepository;
import com.toyota.salesservice.dao.SalesRepository;
import com.toyota.salesservice.dao.SalesSpecification;
import com.toyota.salesservice.domain.Sales;
import com.toyota.salesservice.domain.SalesItems;
import com.toyota.salesservice.dto.requests.CreateReturnRequest;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service implementation for managing sales.
 */

@Service
@Transactional
@AllArgsConstructor
public class SalesManager implements SalesService {
    private final SalesRepository salesRepository;
    private final SalesItemRepository salesItemRepository;
    private final Logger logger = LogManager.getLogger(SalesService.class);
    private final ModelMapperService modelMapperService;
    private final SalesBusinessRules salesBusinessRules;

    /**
     * Adds a new sales record.
     *
     * @param createSalesRequest the sales request data
     * @return the response containing the created sales data
     */
    @Override
    public GetAllSalesResponse addSales(CreateSalesRequest createSalesRequest) {
        logger.info("Adding new sales record created by '{}'.", createSalesRequest.getCreatedBy());
        Sales sales = new Sales();
        sales.setSalesNumber(UUID.randomUUID().toString().substring(0, 8));
        sales.setSalesDate(LocalDateTime.now());
        sales.setCreatedBy(createSalesRequest.getCreatedBy());
        this.salesBusinessRules.validatePaymentType(sales, createSalesRequest);
        logger.debug("Payment type validated for sales record created by '{}'.", createSalesRequest.getCreatedBy());

        List<InventoryRequest> inventoryRequests = createSalesRequest.getCreateSalesItemsRequests().stream()
                .map(salesItem -> this.modelMapperService.forRequest().map(salesItem, InventoryRequest.class)).toList();

        List<InventoryResponse> inventoryResponses = this.salesBusinessRules.checkInInventory(inventoryRequests);
        logger.debug("Received {} inventory responses for sales record created by '{}'.", inventoryResponses.size(), createSalesRequest.getCreatedBy());

        if (!inventoryResponses.isEmpty()) {
            List<SalesItems> salesItems = this.salesBusinessRules.createSalesItems(createSalesRequest, sales, inventoryResponses);
            sales.setSalesItemsList(salesItems);
            this.salesBusinessRules.updateSalesItems(sales, salesItems, inventoryRequests);

            double totalPrice = salesItems.stream()
                    .mapToDouble(SalesItems::getTotalPrice)
                    .sum();
            sales.setTotalPrice(totalPrice);
            logger.debug("Total price calculated for sales record: {}.", totalPrice);

            double money = this.salesBusinessRules.calculateTotalMoney(createSalesRequest, salesItems, inventoryRequests);
            sales.setMoney(money);
            logger.debug("Total money calculated for sales record: {}.", money);

            if (sales.getMoney() >= totalPrice) {
                sales.setChange(sales.getMoney() - sales.getTotalPrice());
                this.salesRepository.save(sales);
                logger.info("Sales record added successfully with sales number '{}'.", sales.getSalesNumber());
                return this.modelMapperService.forResponse().map(sales, GetAllSalesResponse.class);
            } else {
                this.salesBusinessRules.updateInventory(inventoryRequests);
                logger.warn("Insufficient balance for sales record created by '{}'.", createSalesRequest.getCreatedBy());
                throw new InsufficientBalanceException("Insufficient balance");
            }
        } else {
            logger.warn("Error while fetching inventory response for sales record created by '{}'.", createSalesRequest.getCreatedBy());
            throw new FetchInventoryResponseException("Error while fetching inventory response");
        }
    }

    /**
     * Processes return requests for sales items.
     *
     * @param createReturnRequests the list of return requests
     * @return the list of responses for each returned sales item
     */
    @Override
    public List<GetAllSalesItemsResponse> toReturn(List<CreateReturnRequest> createReturnRequests) {
        logger.info("Processing return requests for {} items.", createReturnRequests.size());
        List<InventoryRequest> inventoryRequests = new ArrayList<>();
        List<GetAllSalesItemsResponse> responses = new ArrayList<>();
        for (CreateReturnRequest createReturnRequest : createReturnRequests) {
            logger.info("Processing return request for sales number '{}', barcode number '{}'.",
                    createReturnRequest.getSalesNumber(), createReturnRequest.getBarcodeNumber());
            Optional<Sales> optionalSales = this.salesRepository.findBySalesNumberAndDeletedFalse(createReturnRequest.getSalesNumber());
            if (optionalSales.isEmpty()) {
                logger.warn("Sales not found for sales number '{}'.", createReturnRequest.getSalesNumber());
                throw new SalesNotFoundException("Sales not found: " + createReturnRequest.getSalesNumber());
            }
            Sales sales = optionalSales.get();
            LocalDateTime salesDate = sales.getSalesDate();
            LocalDateTime returnDate = createReturnRequest.getReturnDate();
            this.salesBusinessRules.validateReturnPeriod(salesDate, returnDate);
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
                logger.warn("Sales items not found for barcode number '{}'.", createReturnRequest.getBarcodeNumber());
                throw new SalesItemsNotFoundException("Sales items not found: " + createReturnRequest.getBarcodeNumber());
            }
            int returnQuantity = createReturnRequest.getQuantity();
            if (salesItem.getQuantity() < returnQuantity) {
                logger.warn("Quantity incorrect entry for barcode number '{}'. Requested: {}, Available: {}.",
                        createReturnRequest.getBarcodeNumber(), returnQuantity, salesItem.getQuantity());
                throw new QuantityIncorrectEntryException("Quantity incorrect entry: " + salesItem.getName());
            }
            InventoryRequest inventoryRequest = new InventoryRequest();
            inventoryRequest.setBarcodeNumber(createReturnRequest.getBarcodeNumber());
            inventoryRequest.setQuantity(returnQuantity);
            inventoryRequests.add(inventoryRequest);
            double price = salesItem.getTotalPrice();
            salesItem.setTotalPrice(salesItem.getTotalPrice() - ((price / salesItem.getQuantity()) * returnQuantity));
            sales.setTotalPrice(sales.getTotalPrice() - ((price / salesItem.getQuantity()) * returnQuantity));
            sales.setChange(sales.getChange() + ((price / salesItem.getQuantity()) * returnQuantity));
            salesItem.setQuantity(salesItem.getQuantity() - returnQuantity);
            this.salesRepository.save(sales);
            GetAllSalesItemsResponse response = this.modelMapperService.forResponse().map(salesItem, GetAllSalesItemsResponse.class);
            responses.add(response);
            logger.info("Processed return for barcode number '{}', returned quantity: {}.", createReturnRequest.getBarcodeNumber(), returnQuantity);
        }
        this.salesBusinessRules.updateInventory(inventoryRequests);
        logger.info("Inventory updated for returned items.");
        return responses;
    }

    /**
     * Soft deletes a sales record by its sales number.
     * Marks the sales record and its associated sales items as deleted.
     * Updates the inventory accordingly.
     *
     * @param salesNumber the sales number of the sales record to delete
     * @return a response containing the details of the deleted sales record
     * @throws SalesNotFoundException if no sales record is found with the given sales number
     */
    @Override
    public GetAllSalesResponse deleteSales(String salesNumber) {
        logger.info("Attempting to delete sales record with sales number '{}'.", salesNumber);
        Optional<Sales> optionalSales = this.salesRepository.findBySalesNumberAndDeletedFalse(salesNumber);
        if (optionalSales.isPresent()) {
            Sales sales = optionalSales.get();
            logger.debug("Sales record found with sales number '{}', proceeding with deletion.", salesNumber);

            List<SalesItems> salesItems = sales.getSalesItemsList();
            List<InventoryRequest> inventoryRequests = salesItems.stream()
                    .map(salesItem -> this.modelMapperService.forRequest().map(salesItem, InventoryRequest.class)).toList();
            logger.debug("Updating inventory for {} sales items.", salesItems.size());

            this.salesBusinessRules.updateInventory(inventoryRequests);
            logger.debug("Inventory updated successfully for sales number '{}'.", salesNumber);

            salesItems.forEach(salesItem -> salesItem.setDeleted(true));
            this.salesItemRepository.saveAll(salesItems);
            logger.debug("Sales items marked as deleted for sales number '{}'.", salesNumber);

            sales.setDeleted(true);
            this.salesRepository.save(sales);
            logger.info("Sales record marked as deleted successfully with sales number '{}'.", salesNumber);

            return this.modelMapperService.forResponse().map(sales, GetAllSalesResponse.class);
        } else {
            logger.warn("Sales record not found for sales number '{}'.", salesNumber);
            throw new SalesNotFoundException("Sales not found: " + salesNumber);
        }
    }

    /**
     * Retrieves sales statistics for a given period.
     *
     * @param startDate the start date of the period
     * @param endDate   the end date of the period
     * @return a Map containing the sales statistics:
     * - "totalSales": the total sales amount within the specified period
     * - "averageSales": the average sales amount per transaction within the specified period (0.0 if no sales)
     * - "totalSalesCount": the total number of sales transactions within the specified period
     */
    @Override
    public Map<String, Object> getSalesStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("Getting sales statistics from '{}' to '{}'.", startDate, endDate);

        List<Sales> salesList = this.salesRepository.findBySalesDateBetweenAndDeletedFalse(startDate, endDate);
        double totalSales = salesList.stream().mapToDouble(Sales::getTotalPrice).sum();
        double averageSales = salesList.isEmpty() ? 0.0 : totalSales / salesList.size();
        long totalSalesCount = salesList.size();

        Map<String, Object> response = new HashMap<>();
        response.put("totalSales", totalSales);
        response.put("averageSales", averageSales);
        response.put("totalSalesCount", totalSalesCount);
        logger.debug("Retrieved sales statistics: Total Sales = {}, Average Sales = {}, Total Sales Count = {}", totalSales, averageSales, totalSalesCount);
        return response;
    }

    /**
     * Gets the sorting direction.
     *
     * @param direction the direction string
     * @return the Sort.Direction object
     */
    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }

    /**
     * Gets the sort orders from the sort array.
     *
     * @param sort the sort array
     * @return the list of Sort.Order objects
     */
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

    /**
     * Fetches sales records with pagination and filtering.
     *
     * @param page        the page number
     * @param size        the page size
     * @param sort        the sort criteria
     * @param id          the sales ID
     * @param salesNumber the sales number
     * @param salesDate   the sales date
     * @param createdBy   the creator of the sales record
     * @param paymentType the payment type
     * @param totalPrice  the total price of the sales
     * @param money       the money involved in the sales
     * @param change      the change given in the sales
     * @return the pagination response containing the sales data
     */
    @Override
    public PaginationResponse<GetAllSalesResponse> getSalesFiltered(int page, int size, String[] sort, Long id, String salesNumber,
                                                                    String salesDate, String createdBy, String paymentType, Double totalPrice, Double money, Double change) {
        logger.info("Fetching all sales with pagination. Page: {}, Size: {}, Sort: {}.", page, size, Arrays.toString(sort));
        Pageable pagingSort = PageRequest.of(page, size, Sort.by(getOrder(sort)));
        Specification<Sales> specification = SalesSpecification.filterByCriteria(id, salesNumber, salesDate, createdBy, paymentType, totalPrice, money, change);
        Page<Sales> pageSales = salesRepository.findAll(specification, pagingSort);
        logger.debug("Total sales fetched: {}. Total pages: {}.", pageSales.getTotalElements(), pageSales.getTotalPages());
        List<GetAllSalesResponse> responses = pageSales.getContent().stream()
                .map(sales -> this.modelMapperService.forResponse()
                        .map(sales, GetAllSalesResponse.class)).toList();
        logger.debug("Mapped {} sales records to response objects.", responses.size());

        return new PaginationResponse<>(responses, pageSales);
    }
}
