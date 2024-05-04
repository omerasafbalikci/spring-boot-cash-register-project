package com.toyota.reportservice.resource;

import com.toyota.reportservice.dto.responses.GetAllReportsResponse;
import com.toyota.reportservice.dto.responses.PaginationResponse;
import com.toyota.reportservice.service.abstracts.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/report")
@AllArgsConstructor
public class ReportController {
    ReportService reportService;

    public Mono<PaginationResponse<GetAllReportsResponse>> getAllSalesPage(@RequestParam(defaultValue = "0") int page,
                                                                           @RequestParam(defaultValue = "3") int size,
                                                                           @RequestParam(defaultValue = "id,asc") String[] sort,
                                                                           @RequestParam(defaultValue = "") Long id,
                                                                           @RequestParam(defaultValue = "") String salesNumber,
                                                                           @RequestParam(defaultValue = "") LocalDateTime salesDate,
                                                                           @RequestParam(defaultValue = "") String createdBy,
                                                                           @RequestParam(defaultValue = "") String paymentType,
                                                                           @RequestParam(defaultValue = "") Double totalPrice,
                                                                           @RequestParam(defaultValue = "") Double money,
                                                                           @RequestParam(defaultValue = "") Double change) {
        return this.reportService.getAllSalesPage(page, size, sort, id, salesNumber,
                salesDate, createdBy, paymentType, totalPrice, money, change);
    }
}
