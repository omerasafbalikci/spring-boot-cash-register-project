package com.toyota.reportservice.service.concretes;

import com.toyota.reportservice.dto.responses.GetAllReportDetailsResponse;
import com.toyota.reportservice.dto.responses.GetAllReportsResponse;
import com.toyota.reportservice.dto.responses.PaginationResponse;
import com.toyota.reportservice.service.abstracts.ReportService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


@Service
@Transactional
@AllArgsConstructor
public class ReportManager implements ReportService {
    private final Logger logger = LogManager.getLogger(ReportService.class);
    private final WebClient.Builder webClientBuilder;

    @Override
    public Mono<PaginationResponse<GetAllReportsResponse>> getAllSalesPage(int page, int size, String[] sort, Long id, String salesNumber,
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
                .bodyToMono(new ParameterizedTypeReference<PaginationResponse<GetAllReportsResponse>>() {});
    }

    public byte[] generatePdfReport(List<GetAllReportsResponse> reports) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.beginText();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
            contentStream.newLineAtOffset(100, 700);
            contentStream.showText("SATIS RAPORU");
            contentStream.newLine();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);

            for (GetAllReportsResponse report : reports) {
                contentStream.newLine();
                contentStream.showText("TARIH : " + report.getSalesDate());
                contentStream.newLine();
                contentStream.showText("SATIS NO : " + report.getSalesNumber());
                contentStream.newLine();
                contentStream.showText("KASIYER : " + report.getCreatedBy());
                contentStream.newLine();
                contentStream.showText("SATIS : " + report.getPaymentType());
                contentStream.newLine();
                contentStream.showText("------------------------------------------------------");
                for (GetAllReportDetailsResponse reportDetails : report.getReportDetailsList()) {
                    contentStream.newLine();
                    contentStream.showText(reportDetails.getBarcodeNumber() + "    (" + reportDetails.getQuantity() + " ADET X " + reportDetails.getUnitPrice() + ")");
                    contentStream.newLine();
                    contentStream.showText(reportDetails.getName() + "          " + (reportDetails.getUnitPrice() * reportDetails.getQuantity()));
                }
                contentStream.newLine();
                contentStream.showText("------------------------------------------------------");
                contentStream.showText("ALINAN PARA : " + report.getMoney());
                contentStream.newLine();
                contentStream.showText("PARA USTU : " + report.getChange());
                contentStream.newLine();
                contentStream.showText("------------------------------------------------------");
                contentStream.newLine();
                contentStream.showText("GENEL TOPLAM : " + report.getTotalPrice());
                contentStream.newLine();
                contentStream.showText("KDV FISI DEGILDIR");
                contentStream.newLine();
            }

            contentStream.endText();
        }

        byte[] pdfBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            document.save(outputStream);
            pdfBytes = outputStream.toByteArray();
        }

        document.close();
        return pdfBytes;
    }


}
