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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Service implementation for managing reports.
 */

@Service
@Transactional
@AllArgsConstructor
public class ReportManager implements ReportService {
    private final Logger logger = LogManager.getLogger(ReportService.class);
    private final WebClient.Builder webClientBuilder;
    private final ReportBusinessRules reportBusinessRules;

    /**
     * Retrieves a paginated list of sales reports from the sales service.
     *
     * @param page        the page number to retrieve
     * @param size        the number of records per page
     * @param sort        sorting criteria
     * @param id          optional filter by report ID
     * @param salesNumber optional filter by sales number
     * @param salesDate   optional filter by sales date
     * @param createdBy   optional filter by creator
     * @param paymentType optional filter by payment type
     * @param totalPrice  optional filter by total price
     * @param money       optional filter by money
     * @param change      optional filter by change
     * @return a Mono containing a PaginationResponse with the requested page of sales reports
     */
    @Override
    public Mono<PaginationResponse<GetAllReportsResponse>> getAllSalesPage(int page, int size, String[] sort, Long id, String salesNumber,
                                                                           String salesDate, String createdBy, String paymentType,
                                                                           Double totalPrice, Double money, Double change) {
        logger.info("Requesting sales report page with parameters - page: {}, size: {}, sort: {}, id: {}, salesNumber: {}, salesDate: {}, createdBy: {}, paymentType: {}, totalPrice: {}, money: {}, change: {}",
                page, size, sort, id, salesNumber, salesDate, createdBy, paymentType, totalPrice, money, change);
        return this.webClientBuilder.build().get()
                .uri("http://sales-service/api/sales/get-all", uriBuilder ->
                        uriBuilder
                                .queryParam("page", page)
                                .queryParam("size", size)
                                .queryParam("sort", (Object[]) sort)
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
                .bodyToMono(new ParameterizedTypeReference<PaginationResponse<GetAllReportsResponse>>() {
                })
                .doOnSuccess(response -> {
                    if (response != null) {
                        logger.info("Successfully retrieved sales report page - page: {}, size: {}, total elements: {}",
                                page, size, response.getPageable().getTotalElements());
                    } else {
                        logger.warn("Sales report page retrieval returned null - page: {}, size: {}", page, size);
                    }
                })
                .doOnError(error -> logger.error("Error occurred while retrieving sales report page - page: {}, size: {}", page, size, error));
    }

    /**
     * Generates a PDF report for a given sales number.
     *
     * @param salesNumber the sales number for which the PDF report is to be generated
     * @return a byte array containing the generated PDF report
     * @throws IOException if an error occurs during PDF generation
     */
    public byte[] generatePdfReport(String salesNumber) throws IOException {
        logger.info("Generating PDF report for sales number: {}", salesNumber);
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

            logger.info("Filling PDF document with report data for sales number: {}", salesNumber);
            this.reportBusinessRules.pdf(report, document, page);

            byte[] pdfBytes;
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                document.save(outputStream);
                pdfBytes = outputStream.toByteArray();
                logger.info("PDF report generated successfully for sales number: {}", salesNumber);
            }

            document.close();
            return pdfBytes;
        } else {
            logger.error("Report not found for sales number: {}", salesNumber);
            throw new ReportNotFoundException("Report not found");
        }
    }
}
