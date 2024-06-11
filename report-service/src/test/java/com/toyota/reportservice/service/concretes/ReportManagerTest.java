package com.toyota.reportservice.service.concretes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.toyota.reportservice.dto.responses.CustomPageable;
import com.toyota.reportservice.dto.responses.GetAllReportDetailsResponse;
import com.toyota.reportservice.dto.responses.GetAllReportsResponse;
import com.toyota.reportservice.dto.responses.PaginationResponse;
import com.toyota.reportservice.service.rules.ReportBusinessRules;
import com.toyota.reportservice.utilities.exceptions.ReportNotFoundException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
public class ReportManagerTest {
    @Mock
    private WebClient.Builder webClientBuilder;
    @Mock
    private WebClient.ResponseSpec responseSpec;
    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;
    @Mock
    private WebClient.RequestBodySpec requestBodySpec;
    @Mock
    private ReportBusinessRules reportBusinessRules;
    private ReportManager reportManager;

    @BeforeEach
    void setUp() {
        reportManager = new ReportManager(webClientBuilder, reportBusinessRules);
    }

    @Test
    void generatePdfReport_success() {

    }

    @Test
    void generatePdfReport_reportNotFound() {

    }
}
