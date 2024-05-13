package com.toyota.reportservice.service.abstracts;

import com.toyota.reportservice.dto.responses.GetAllReportsResponse;
import com.toyota.reportservice.dto.responses.PaginationResponse;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface ReportService {
    Mono<PaginationResponse<GetAllReportsResponse>> getAllSalesPage(int page, int size, String[] sort, Long id, String salesNumber,
                                                     String salesDate, String createdBy, String paymentType,
                                                     Double totalPrice, Double money, Double change);
    byte[] generatePdfReport(String salesNumber) throws IOException;
}
