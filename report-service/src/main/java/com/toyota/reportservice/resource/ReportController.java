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
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reports")
@AllArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping()
    public Mono<PaginationResponse<GetAllReportsResponse>> getAllSalesPage(@RequestParam(defaultValue = "0") int page,
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

    @GetMapping("/generate-pdf")
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
