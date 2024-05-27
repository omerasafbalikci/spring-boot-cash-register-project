package com.toyota.reportservice.service.rules;

import com.toyota.reportservice.dto.responses.GetAllReportDetailsResponse;
import com.toyota.reportservice.dto.responses.GetAllReportsResponse;
import com.toyota.reportservice.service.concretes.ReportManager;
import lombok.AllArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Service for handling business rules related to reports.
 */

@Service
@AllArgsConstructor
public class ReportBusinessRules {
    /**
     * Generates a PDF report with the given sales report data.
     *
     * @param report   the sales report data to include in the PDF
     * @param document the PDDocument to which the content will be added
     * @param page     the PDPage to which the content will be added
     * @throws IOException if an I/O error occurs while writing to the PDF document
     */
    public void pdf(GetAllReportsResponse report, PDDocument document, PDPage page) throws IOException {
        PDType0Font font = PDType0Font.load(document, ReportManager.class.getResourceAsStream("/org/apache/pdfbox/resources/ttf/LiberationSans-Regular.ttf"));
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.beginText();
            contentStream.setFont(font, 12);
            contentStream.newLineAtOffset(200, 700);
            contentStream.showText("             SATIS RAPORU");
            contentStream.newLineAtOffset(0, -20);
            contentStream.setFont(font, 10);
            contentStream.showText("TARIH : " + report.getSalesDate());
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("SATIS NO : " + report.getSalesNumber());
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("KASIYER : " + report.getCreatedBy());
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("SATIS : " + report.getPaymentType());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("------------------------------------------------------");

            for (GetAllReportDetailsResponse reportDetails : report.getSalesItemsList()) {
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText(reportDetails.getBarcodeNumber() + "    (" + reportDetails.getQuantity() + " ADET X " + reportDetails.getUnitPrice() + ")");
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText(reportDetails.getName() + "                                      " + (reportDetails.getTotalPrice()));
            }

            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("------------------------------------------------------");
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("ALINAN PARA : " + report.getMoney());
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("PARA USTU : " + report.getChange());
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("------------------------------------------------------");
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("GENEL TOPLAM : " + report.getTotalPrice());
            contentStream.newLineAtOffset(40, -30);
            contentStream.showText("KDV FISI DEGILDIR");
            contentStream.endText();
        }
    }
}

