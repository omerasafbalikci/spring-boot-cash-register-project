package com.toyota.reportservice.service.concretes;

import com.toyota.reportservice.dto.responses.GetAllReportsResponse;
import com.toyota.reportservice.dto.responses.PaginationResponse;
import com.toyota.reportservice.service.rules.ReportBusinessRules;
import com.toyota.reportservice.utilities.exceptions.ReportNotFoundException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportManagerTest {
    @Mock
    private WebClient.Builder webClientBuilder;
    @Mock
    private ReportBusinessRules reportBusinessRules;
    private ReportManager reportManager;

    @BeforeEach
    void setUp() {
        reportManager = new ReportManager(webClientBuilder, reportBusinessRules);
    }

    @Test
    void generatePdfReport_success() throws IOException {
        String salesNumber = "12345";
        GetAllReportsResponse reportResponse = new GetAllReportsResponse();
        reportResponse.setSalesNumber(salesNumber);
        reportResponse.setSalesDate(LocalDateTime.now());
        reportResponse.setCreatedBy("Asaf");
        reportResponse.setPaymentType("CARD");
        reportResponse.setSalesItemsList(new ArrayList<>());
        reportResponse.setMoney(100.0);
        reportResponse.setChange(10.0);
        reportResponse.setTotalPrice(90.0);

        PaginationResponse<GetAllReportsResponse> paginationResponse = new PaginationResponse<>();
        paginationResponse.setContent(List.of(reportResponse));

        ReportManager reportManagerSpy = spy(reportManager);
        doReturn(Mono.just(paginationResponse)).when(reportManagerSpy).getAllSalesPage(anyInt(), anyInt(), any(), any(), eq(salesNumber), any(), any(), any(), any(), any(), any());

        byte[] pdfBytes = reportManagerSpy.generatePdfReport(salesNumber);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
        verify(reportBusinessRules, times(1)).pdf(eq(reportResponse), any(PDDocument.class), any(PDPage.class));
    }

    @Test
    void generatePdfReport_reportNotFound() {
        String salesNumber = "12345";

        PaginationResponse<GetAllReportsResponse> paginationResponse = new PaginationResponse<>();
        paginationResponse.setContent(new ArrayList<>());

        ReportManager reportManagerSpy = spy(reportManager);
        doReturn(Mono.just(paginationResponse)).when(reportManagerSpy).getAllSalesPage(anyInt(), anyInt(), any(), any(), eq(salesNumber), any(), any(), any(), any(), any(), any());

        ReportNotFoundException exception = assertThrows(ReportNotFoundException.class, () -> reportManagerSpy.generatePdfReport(salesNumber));
        assertEquals("Report not found", exception.getMessage());
    }
}
