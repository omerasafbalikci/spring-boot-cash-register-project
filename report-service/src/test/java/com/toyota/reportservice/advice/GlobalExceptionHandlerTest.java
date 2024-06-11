package com.toyota.reportservice.advice;

import com.toyota.reportservice.utilities.exceptions.ReportNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.context.request.ServletWebRequest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        this.globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleUserReportFoundException() {
        // Given
        String message = "Report not found";
        ReportNotFoundException reportNotFoundException = new ReportNotFoundException(message);

        // When
        String path = "/test";
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(path);
        ResponseEntity<Object> response = this.globalExceptionHandler.handleReportNotFoundException(reportNotFoundException, request);

        // Then
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        Assertions.assertNotNull(errorResponse);
        Assertions.assertTrue(HttpStatus.NOT_FOUND.getReasonPhrase().equalsIgnoreCase(errorResponse.getError()),
                "Expected error: " + HttpStatus.NOT_FOUND.getReasonPhrase() + ", but got: " + errorResponse.getError());
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus());
        Assertions.assertEquals(message, errorResponse.getMessage());
        Assertions.assertEquals(path, errorResponse.getPath());
    }

    @Test
    void handleHttpMessageNotReadable() {
        // Given
        HttpMessageNotReadableException messageNotReadableException = mock(HttpMessageNotReadableException.class);
        HttpHeaders headers = new HttpHeaders();
        ServletWebRequest servletWebRequest = mock(ServletWebRequest.class);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        String path = "/test";

        // When
        Mockito.when(servletWebRequest.getRequest()).thenReturn(httpServletRequest);
        when(httpServletRequest.getRequestURI()).thenReturn(path);
        ResponseEntity<Object> response = globalExceptionHandler.handleHttpMessageNotReadable(
                messageNotReadableException, headers, HttpStatus.BAD_REQUEST, servletWebRequest);

        // Then
        assertNotNull(response);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        Assertions.assertNotNull(errorResponse);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase().toLowerCase(), errorResponse.getError().toLowerCase(),
                "Expected error: " + HttpStatus.BAD_REQUEST.getReasonPhrase() + ", but got: " + errorResponse.getError());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        Assertions.assertEquals(path, errorResponse.getPath());
    }
}
