package com.toyota.reportservice.service.concretes;

import com.toyota.reportservice.dto.responses.GetAllReportsResponse;
import com.toyota.reportservice.service.abstracts.ReportService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeMap;


@Service
@Transactional
@AllArgsConstructor
public class ReportManager implements ReportService {
    private final Logger logger = LogManager.getLogger(ReportService.class);
    private final WebClient.Builder webClientBuilder;

    Mono<List<GetAllReportsResponse>> getAllSalesPage(int page, int size, String[] sort, Long id, String salesNumber,
                                                      LocalDateTime salesDate, String createdBy, String paymentType,
                                                      Double totalPrice, Double money, Double change) {
        return webClientBuilder.build()
                .get()
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
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<GetAllReportsResponse>>() {});
    }

    Mono<Void> generateSalesPdf(String salesNumber, String fileName) {
        return getAllSalesPage(0, 1, null, null, salesNumber, null, null, null, null, null, null)
                .flatMap(reportResponses -> {
                    if (!reportResponses.isEmpty()) {
                        GetAllReportsResponse reportResponse = reportResponses.get(0);
                        return createPdf(reportResponse, fileName);
                    } else {
                        return Mono.error(new Exception("Sales not found for the given sales number"));
                    }
                });
    }

    private Mono<Void> createPdf(GetAllReportsResponse reportResponse, String fileName) {
        return Mono.fromRunnable(() -> {
            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage();
                document.addPage(page);

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(100, 700);
                    contentStream.showText("Sales Number: " + reportResponse.getSalesNumber());
                    contentStream.newLine();
                    contentStream.showText("Sales Date: " + reportResponse.getSalesDate());
                    contentStream.newLine();
                    contentStream.showText("Created By: " + reportResponse.getCreatedBy());
                    contentStream.newLine();
                    // Diğer satış bilgilerini buraya ekleyin...
                    contentStream.endText();
                }

                document.save(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
