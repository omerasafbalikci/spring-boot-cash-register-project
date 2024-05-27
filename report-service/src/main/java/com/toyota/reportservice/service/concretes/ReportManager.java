package com.toyota.reportservice.service.concretes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.toyota.reportservice.dto.responses.GetAllReportsResponse;
import com.toyota.reportservice.dto.responses.PaginationResponse;
import com.toyota.reportservice.service.abstracts.ReportService;
import com.toyota.reportservice.service.rules.ReportBusinessRules;
import com.toyota.reportservice.utilities.exceptions.ReportNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;


@Service
@Transactional
@AllArgsConstructor
public class ReportManager implements ReportService {
    private final Logger logger = LogManager.getLogger(ReportService.class);
    private final WebClient.Builder webClientBuilder;
    private final ReportBusinessRules reportBusinessRules;

    @Override
    public Mono<PaginationResponse<GetAllReportsResponse>> getAllSalesPage(int page, int size, String[] sort, Long id, String salesNumber,
                                                                           String salesDate, String createdBy, String paymentType,
                                                                           Double totalPrice, Double money, Double change) {
        return this.webClientBuilder.build().get()
                .uri("http://sales-service/api/sales", uriBuilder ->
                        uriBuilder
                                .queryParam("page", page)
                                .queryParam("size", size)
                                .queryParam("sort", sort)
                                .queryParam("id", id)
                                .queryParam("salesNumber", salesNumber)
                                .queryParam("salesDate", salesDate)
                                .queryParam("createdBy", createdBy)
                                .queryParam("paymentType", paymentType)
                                .queryParam("totalPrice", totalPrice)
                                .queryParam("money", money)
                                .queryParam("change", change)
                                .build()
                )
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PaginationResponse<GetAllReportsResponse>>() {})
                .doOnSuccess(response -> {
                    if (response != null) {
                        logger.info("Page: {} Size: {}, Total:{}",
                                page, size, response.getPageable().getTotalElements());
                    }
                });
    }


    public byte[] generatePdfReport(String salesNumber) throws IOException {
        Mono<PaginationResponse<GetAllReportsResponse>> reportMono = getAllSalesPage(0, 1, null, null, salesNumber,
                null, null, null, null, null, null);

        PaginationResponse<GetAllReportsResponse> reportResponse = reportMono.block();

        if (reportResponse != null && reportResponse.getContent() != null && !reportResponse.getContent().isEmpty()) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            GetAllReportsResponse report = objectMapper.convertValue(reportResponse.getContent().get(0), GetAllReportsResponse.class);

            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            this.reportBusinessRules.pdf(report, document, page);

            byte[] pdfBytes;
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                document.save(outputStream);
                pdfBytes = outputStream.toByteArray();
            }

            document.close();
            return pdfBytes;
        } else {
            throw new ReportNotFoundException("Report not found");
        }
    }
}
