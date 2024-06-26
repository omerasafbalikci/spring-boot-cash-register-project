package com.toyota.salesservice.service.abstracts;

import com.toyota.salesservice.dto.requests.CreateReturnRequest;
import com.toyota.salesservice.dto.requests.CreateSalesRequest;
import com.toyota.salesservice.dto.responses.GetAllSalesItemsResponse;
import com.toyota.salesservice.dto.responses.GetAllSalesResponse;
import com.toyota.salesservice.dto.responses.PaginationResponse;
import com.toyota.salesservice.utilities.exceptions.SalesNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeMap;

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
     * Deletes a sales record by its sales number.
     *
     * @param salesNumber the sales number
     * @return the response containing the deleted sales data
     * @throws SalesNotFoundException if the sales record is not found
     */
    GetAllSalesResponse deleteSales(String salesNumber);

    /**
     * Retrieves sales statistics for a given period.
     *
     * @param startDate the start date of the period to retrieve statistics for
     * @param endDate the end date of the period to retrieve statistics for
     * @return a TreeMap containing the sales statistics:
     *         - "totalSales": the total sales amount within the specified period
     *         - "averageSales": the average sales amount per transaction within the specified period
     *         - "totalSalesCount": the total number of sales transactions within the specified period
     */
    TreeMap<String, Object> getSalesStatistics(LocalDateTime startDate, LocalDateTime endDate);

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
