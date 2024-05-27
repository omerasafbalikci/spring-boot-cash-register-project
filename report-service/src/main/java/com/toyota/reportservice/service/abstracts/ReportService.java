package com.toyota.reportservice.service.abstracts;

import com.toyota.reportservice.dto.responses.GetAllReportsResponse;
import com.toyota.reportservice.dto.responses.PaginationResponse;
import reactor.core.publisher.Mono;

import java.io.IOException;

/**
 * Interface for report's service class.
 */

public interface ReportService {
    /**
     * Retrieves a paginated list of sales reports based on the provided filters.
     *
     * @param page        the page number to retrieve
     * @param size        the number of records per page
     * @param sort        the sorting criteria
     * @param id          the ID of the report to filter by, optional
     * @param salesNumber the sales number to filter by, optional
     * @param salesDate   the sales date to filter by, optional
     * @param createdBy   the creator to filter by, optional
     * @param paymentType the payment type to filter by, optional
     * @param totalPrice  the total price to filter by, optional
     * @param money       the money amount to filter by, optional
     * @param change      the change amount to filter by, optional
     * @return a Mono emitting the paginated response with the list of sales reports
     */
    Mono<PaginationResponse<GetAllReportsResponse>> getAllSalesPage(int page, int size, String[] sort, Long id, String salesNumber,
                                                                    String salesDate, String createdBy, String paymentType,
                                                                    Double totalPrice, Double money, Double change);
    /**
     * Generates a PDF report for a specific sales number.
     *
     * @param salesNumber the sales number for which to generate the report
     * @return a byte array representing the generated PDF report
     * @throws IOException if an I/O error occurs during PDF generation
     */
    byte[] generatePdfReport(String salesNumber) throws IOException;
}
