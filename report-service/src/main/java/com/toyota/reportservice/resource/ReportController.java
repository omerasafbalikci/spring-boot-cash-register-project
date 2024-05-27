package com.toyota.reportservice.resource;

import com.toyota.reportservice.dto.responses.GetAllReportsResponse;
import com.toyota.reportservice.dto.responses.PaginationResponse;
import com.toyota.reportservice.service.abstracts.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;

/**
 * REST controller for managing reports.
 */

@RestController
@RequestMapping("/api/reports")
@AllArgsConstructor
public class ReportController {
    private final ReportService reportService;

    /**
     * Retrieves a paginated list of sales reports.
     *
     * @param page        the page number to retrieve, default is 0
     * @param size        the number of records per page, default is 3
     * @param sort        the sorting criteria, default is "id,asc"
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
    @GetMapping()
    public Mono<PaginationResponse<GetAllReportsResponse>> getAllSalesPage(
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
            @RequestParam(defaultValue = "") Double change) {
        return this.reportService.getAllSalesPage(page, size, sort, id, salesNumber,
                salesDate, createdBy, paymentType, totalPrice, money, change);
    }

    /**
     * Generates a PDF report for a specific sales number.
     *
     * @param salesNumber the sales number for which to generate the report
     * @return a ResponseEntity containing the PDF byte array if successful,
     *         or an error message if the report generation fails
     */
   @GetMapping("/generate.pdf")
   public ResponseEntity<byte[]> generatePdfReport(@RequestParam String salesNumber) {
        try {
            byte[] pdfBytes = this.reportService.generatePdfReport(salesNumber);
            return ResponseEntity
                    .ok()
                    .header("Content-Disposition", "attachment; filename=report.pdf")
                    .body(pdfBytes);
        } catch (IllegalArgumentException | IOException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(("An error occurred while creating the report: " + e.getMessage()).getBytes());
        }
    }
}
