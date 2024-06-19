package com.toyota.reportservice.resource;

import com.toyota.reportservice.dto.responses.CustomPageable;
import com.toyota.reportservice.dto.responses.GetAllReportsResponse;
import com.toyota.reportservice.dto.responses.PaginationResponse;
import com.toyota.reportservice.service.abstracts.ReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportControllerTest {
    @Mock
    ReportService reportService;
    @InjectMocks
    ReportController reportController;

    @Test
    void testGetAllSalesPage() {
        CustomPageable pageable = new CustomPageable(0, 3, 0, 1);
        PaginationResponse<GetAllReportsResponse> response = new PaginationResponse<>(Collections.emptyList(), pageable);

        when(reportService.getAllSalesPage(anyInt(), anyInt(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(Mono.just(response));

        Mono<PaginationResponse<GetAllReportsResponse>> result = reportController.getAllSalesPage(0, 3, new String[]{"id,asc"}, null, null, null, null, null, null, null, null);

        assertEquals(response, result.block());
    }

    @Test
    void testGeneratePdfReport_Success() throws IOException {
        byte[] pdfBytes = "PDF content".getBytes();
        when(reportService.generatePdfReport(anyString())).thenReturn(pdfBytes);

        ResponseEntity<byte[]> responseEntity = reportController.generatePdfReport("12345");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("attachment; filename=report.pdf", responseEntity.getHeaders().getFirst("Content-Disposition"));
        assertEquals(pdfBytes, responseEntity.getBody());
    }

    @Test
    void testGeneratePdfReport_Failure() throws IOException {
        when(reportService.generatePdfReport(anyString())).thenThrow(new IllegalArgumentException("Invalid sales number"));

        ResponseEntity<byte[]> responseEntity = reportController.generatePdfReport("12345");

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("An error occurred while creating the report: Invalid sales number", new String(Objects.requireNonNull(responseEntity.getBody())));
    }
}
