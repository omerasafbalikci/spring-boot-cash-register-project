package com.toyota.salesservice.resource;

import com.toyota.salesservice.dto.requests.CreateReturnRequest;
import com.toyota.salesservice.dto.requests.CreateSalesRequest;
import com.toyota.salesservice.dto.responses.GetAllSalesItemsResponse;
import com.toyota.salesservice.dto.responses.GetAllSalesResponse;
import com.toyota.salesservice.dto.responses.PaginationResponse;
import com.toyota.salesservice.service.abstracts.SalesService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing sales.
 */

@RestController
@RequestMapping("/api/sales")
@AllArgsConstructor
public class SalesController {
    private final SalesService salesService;

    /**
     * Adds a new sales record.
     *
     * @param createSalesRequest the request body containing the details of the sales to be created
     * @return a ResponseEntity containing the created sales details
     */
    @PostMapping("/add")
    public ResponseEntity<GetAllSalesResponse> addSales(@RequestBody @Valid CreateSalesRequest createSalesRequest) {
        GetAllSalesResponse response = this.salesService.addSales(createSalesRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Processes a return of sales items.
     *
     * @param createReturnRequests the request body containing the details of the items to be returned
     * @return a ResponseEntity containing the details of the returned sales items
     */
    @PostMapping("/return")
    public ResponseEntity<List<GetAllSalesItemsResponse>> toReturn(@RequestBody @Valid List<CreateReturnRequest> createReturnRequests) {
        List<GetAllSalesItemsResponse> response = this.salesService.toReturn(createReturnRequests);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves sales records based on the specified filtering criteria.
     *
     * @param page        the page number to retrieve (default is 0)
     * @param size        the size of the page to retrieve (default is 3)
     * @param sort        the sorting criteria (default is "id,asc")
     * @param id          the ID of the sales record
     * @param salesNumber the sales number
     * @param salesDate   the sales date
     * @param createdBy   the creator of the sales record
     * @param paymentType the payment type
     * @param totalPrice  the total price
     * @param money       the money on sale
     * @param change      the change on sale
     * @return a PaginationResponse containing the filtered sales records
     */
    @GetMapping()
    public PaginationResponse<GetAllSalesResponse> getSalesFiltered(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort,
            @RequestParam(defaultValue = "") Long id,
            @RequestParam(defaultValue = "") String salesNumber,
            @RequestParam(defaultValue = "") String salesDate,
            @RequestParam(defaultValue = "") String createdBy,
            @RequestParam(defaultValue = "") String paymentType,
            @RequestParam(defaultValue = "") Double totalPrice,
            @RequestParam(defaultValue = "") Double money,
            @RequestParam(defaultValue = "") Double change
    ) {
        return this.salesService.getSalesFiltered(page, size, sort, id, salesNumber, salesDate, createdBy, paymentType, totalPrice, money, change);
    }
}
