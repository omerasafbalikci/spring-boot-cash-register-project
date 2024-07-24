package com.toyota.salesservice.service.abstracts;

import com.toyota.salesservice.dto.requests.CreateReturnRequest;
import com.toyota.salesservice.dto.requests.CreateSalesRequest;
import com.toyota.salesservice.dto.responses.GetAllSalesItemsResponse;
import com.toyota.salesservice.dto.responses.GetAllSalesResponse;
import com.toyota.salesservice.dto.responses.PaginationResponse;
import com.toyota.salesservice.utilities.exceptions.SalesNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Interface for sales service class.
 */

public interface SalesService {
    /**
     * Adds a new sales record.
     *
     * @param createSalesRequest the request containing details for creating the sales record
     * @return the response containing the created sales details
     */
    GetAllSalesResponse addSales(CreateSalesRequest createSalesRequest);

    /**
     * Processes returns for the given sales items.
     *
     * @param createReturnRequest the list of requests containing details for returning the sales items
     * @return the list of responses containing the returned sales items details
     */
    List<GetAllSalesItemsResponse> toReturn(List<CreateReturnRequest> createReturnRequest);

    /**
     * Soft deletes a sales record by its sales number.
     * Marks the sales record and its associated sales items as deleted.
     * Updates the inventory accordingly.
     *
     * @param salesNumber the sales number of the sales record to delete
     * @return a response containing the details of the deleted sales record
     * @throws SalesNotFoundException if no sales record is found with the given sales number
     */
    GetAllSalesResponse deleteSales(String salesNumber);

    /**
     * Retrieves sales statistics for a given period.
     *
     * @param startDate the start date of the period to retrieve statistics for
     * @param endDate the end date of the period to retrieve statistics for
     * @return a Map containing the sales statistics:
     *         - "totalSales": the total sales amount within the specified period
     *         - "averageSales": the average sales amount per transaction within the specified period
     *         - "totalSalesCount": the total number of sales transactions within the specified period
     */
    Map<String, Object> getSalesStatistics(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Retrieves sales records filtered by various criteria.
     *
     * @param page the page number to retrieve
     * @param size the number of records per page
     * @param sort the sorting criteria
     * @param id the ID of the sales record
     * @param salesNumber the sales number
     * @param salesDate the date of the sale
     * @param createdBy the creator of the sales record
     * @param paymentType the payment type used in the sale
     * @param totalPrice the total price of the sale
     * @param money the amount of money received
     * @param change the amount of change given
     * @return a pagination response containing the filtered sales records
     */
    PaginationResponse<GetAllSalesResponse> getSalesFiltered(int page, int size, String[] sort, Long id, String salesNumber,
                                                                   String salesDate, String createdBy, String paymentType,
                                                                   Double totalPrice, Double money, Double change);
}
