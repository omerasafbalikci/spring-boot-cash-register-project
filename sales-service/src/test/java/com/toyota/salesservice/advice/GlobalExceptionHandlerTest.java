package com.toyota.salesservice.advice;

import com.toyota.salesservice.utilities.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        this.globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleCampaignNotFoundException() {
        // Given
        String message = "Campaign not found";
        CampaignNotFoundException campaignNotFoundException = new CampaignNotFoundException(message);

        // When
        String path = "/test";
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(path);
        ResponseEntity<Object> response = this.globalExceptionHandler.handleCampaignNotFoundException(campaignNotFoundException, request);

        // Then
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        Assertions.assertNotNull(errorResponse);
        assertTrue(HttpStatus.NOT_FOUND.getReasonPhrase().equalsIgnoreCase(errorResponse.getError()),
                "Expected error: " + HttpStatus.NOT_FOUND.getReasonPhrase() + ", but got: " + errorResponse.getError());
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(path, errorResponse.getPath());
    }

    @Test
    void handleCampaignAlreadyExistsException() {
        // Given
        String message = "Campaign already exists";
        CampaignAlreadyExistsException campaignAlreadyExistsException = new CampaignAlreadyExistsException(message);

        // When
        String path = "/test";
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(path);
        ResponseEntity<Object> response = this.globalExceptionHandler.handleCampaignAlreadyExistsException(campaignAlreadyExistsException, request);

        // Then
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertNotNull(errorResponse);
        assertTrue(HttpStatus.CONFLICT.getReasonPhrase().equalsIgnoreCase(errorResponse.getError()),
                "Expected error: " + HttpStatus.CONFLICT.getReasonPhrase() + ", but got: " + errorResponse.getError());
        assertEquals(HttpStatus.CONFLICT.value(), errorResponse.getStatus());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(path, errorResponse.getPath());
    }

    @Test
    void handleCampaignDetailsAreIncorrectException() {
        // Given
        String message = "Campaign details are incorrect";
        CampaignDetailsAreIncorrectException campaignDetailsAreIncorrectException = new CampaignDetailsAreIncorrectException(message);

        // When
        String path = "/test";
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(path);
        ResponseEntity<Object> response = this.globalExceptionHandler.handleCampaignDetailsAreIncorrectException(campaignDetailsAreIncorrectException, request);

        // Then
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertNotNull(errorResponse);
        assertTrue(HttpStatus.BAD_REQUEST.getReasonPhrase().equalsIgnoreCase(errorResponse.getError()),
                "Expected error: " + HttpStatus.BAD_REQUEST.getReasonPhrase() + ", but got: " + errorResponse.getError());
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(path, errorResponse.getPath());
    }

    @Test
    void handleFetchInventoryResponseException() {
        // Given
        String message = "Failed to fetch inventory response";
        FetchInventoryResponseException fetchInventoryResponseException = new FetchInventoryResponseException(message);

        // When
        String path = "/test";
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(path);
        ResponseEntity<Object> response = this.globalExceptionHandler.handleFetchInventoryResponseException(fetchInventoryResponseException, request);

        // Then
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertNotNull(errorResponse);
        assertTrue(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase().equalsIgnoreCase(errorResponse.getError()),
                "Expected error: " + HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase() + ", but got: " + errorResponse.getError());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(path, errorResponse.getPath());
    }

    @Test
    void handleInsufficientBalanceException() {
        // Given
        String message = "Insufficient balance";
        InsufficientBalanceException insufficientBalanceException = new InsufficientBalanceException(message);

        // When
        String path = "/test";
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(path);
        ResponseEntity<Object> response = this.globalExceptionHandler.handleInsufficientBalanceException(insufficientBalanceException, request);

        // Then
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertNotNull(errorResponse);
        assertTrue(HttpStatus.BAD_REQUEST.getReasonPhrase().equalsIgnoreCase(errorResponse.getError()),
                "Expected error: " + HttpStatus.BAD_REQUEST.getReasonPhrase() + ", but got: " + errorResponse.getError());
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(path, errorResponse.getPath());
    }

    @Test
    void handleNoMoneyEnteredException() {
        // Given
        String message = "No money entered";
        NoMoneyEnteredException noMoneyEnteredException = new NoMoneyEnteredException(message);

        // When
        String path = "/test";
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(path);
        ResponseEntity<Object> response = this.globalExceptionHandler.handleNoMoneyEnteredException(noMoneyEnteredException, request);

        // Then
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertNotNull(errorResponse);
        assertTrue(HttpStatus.BAD_REQUEST.getReasonPhrase().equalsIgnoreCase(errorResponse.getError()),
                "Expected error: " + HttpStatus.BAD_REQUEST.getReasonPhrase() + ", but got: " + errorResponse.getError());
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(path, errorResponse.getPath());
    }

    @Test
    void handlePaymentTypeNotEnteredException() {
        // Given
        String message = "Payment type not entered";
        PaymentTypeNotEnteredException paymentTypeNotEnteredException = new PaymentTypeNotEnteredException(message);

        // When
        String path = "/test";
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(path);
        ResponseEntity<Object> response = this.globalExceptionHandler.handlePaymentTypeNotEnteredException(paymentTypeNotEnteredException, request);

        // Then
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertNotNull(errorResponse);
        assertTrue(HttpStatus.BAD_REQUEST.getReasonPhrase().equalsIgnoreCase(errorResponse.getError()),
                "Expected error: " + HttpStatus.BAD_REQUEST.getReasonPhrase() + ", but got: " + errorResponse.getError());
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(path, errorResponse.getPath());
    }

    @Test
    void handleProductIsNotInStockException() {
        // Given
        String message = "Product is not in stock";
        ProductIsNotInStockException productIsNotInStockException = new ProductIsNotInStockException(message);

        // When
        String path = "/test";
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(path);
        ResponseEntity<Object> response = this.globalExceptionHandler.handleProductIsNotInStockException(productIsNotInStockException, request);

        // Then
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertNotNull(errorResponse);
        assertTrue(HttpStatus.BAD_REQUEST.getReasonPhrase().equalsIgnoreCase(errorResponse.getError()),
                "Expected error: " + HttpStatus.BAD_REQUEST.getReasonPhrase() + ", but got: " + errorResponse.getError());
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(path, errorResponse.getPath());
    }

    @Test
    void handleProductNotFoundException() {
        // Given
        String message = "Product not found";
        ProductNotFoundException productNotFoundException = new ProductNotFoundException(message);

        // When
        String path = "/test";
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(path);
        ResponseEntity<Object> response = this.globalExceptionHandler.handleProductNotFoundException(productNotFoundException, request);

        // Then
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertNotNull(errorResponse);
        assertTrue(HttpStatus.NOT_FOUND.getReasonPhrase().equalsIgnoreCase(errorResponse.getError()),
                "Expected error: " + HttpStatus.NOT_FOUND.getReasonPhrase() + ", but got: " + errorResponse.getError());
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(path, errorResponse.getPath());
    }

    @Test
    void handleProductStatusFalseException() {
        // Given
        String message = "Product status is false";
        ProductStatusFalseException productStatusFalseException = new ProductStatusFalseException(message);

        // When
        String path = "/test";
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(path);
        ResponseEntity<Object> response = this.globalExceptionHandler.handleProductStatusFalseException(productStatusFalseException, request);

        // Then
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertNotNull(errorResponse);
        assertTrue(HttpStatus.BAD_REQUEST.getReasonPhrase().equalsIgnoreCase(errorResponse.getError()),
                "Expected error: " + HttpStatus.BAD_REQUEST.getReasonPhrase() + ", but got: " + errorResponse.getError());
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(path, errorResponse.getPath());
    }

    @Test
    void handleQuantityIncorrectEntryException() {
        // Given
        String message = "Quantity incorrect entry";
        QuantityIncorrectEntryException quantityIncorrectEntryException = new QuantityIncorrectEntryException(message);

        // When
        String path = "/test";
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(path);
        ResponseEntity<Object> response = this.globalExceptionHandler.handleQuantityIncorrectEntryException(quantityIncorrectEntryException, request);

        // Then
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertNotNull(errorResponse);
        assertTrue(HttpStatus.BAD_REQUEST.getReasonPhrase().equalsIgnoreCase(errorResponse.getError()),
                "Expected error: " + HttpStatus.BAD_REQUEST.getReasonPhrase() + ", but got: " + errorResponse.getError());
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(path, errorResponse.getPath());
    }

    @Test
    void handleReturnPeriodExpiredException() {
        // Given
        String message = "Return period expired";
        ReturnPeriodExpiredException returnPeriodExpiredException = new ReturnPeriodExpiredException(message);

        // When
        String path = "/test";
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(path);
        ResponseEntity<Object> response = this.globalExceptionHandler.handleReturnPeriodExpiredException(returnPeriodExpiredException, request);

        // Then
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertNotNull(errorResponse);
        assertTrue(HttpStatus.BAD_REQUEST.getReasonPhrase().equalsIgnoreCase(errorResponse.getError()),
                "Expected error: " + HttpStatus.BAD_REQUEST.getReasonPhrase() + ", but got: " + errorResponse.getError());
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(path, errorResponse.getPath());
    }

    @Test
    void handleSalesItemsNotFoundException() {
        // Given
        String message = "Sales items not found";
        SalesItemsNotFoundException salesItemsNotFoundException = new SalesItemsNotFoundException(message);

        // When
        String path = "/test";
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(path);
        ResponseEntity<Object> response = this.globalExceptionHandler.handleSalesItemsNotFoundException(salesItemsNotFoundException, request);

        // Then
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertNotNull(errorResponse);
        assertTrue(HttpStatus.NOT_FOUND.getReasonPhrase().equalsIgnoreCase(errorResponse.getError()),
                "Expected error: " + HttpStatus.NOT_FOUND.getReasonPhrase() + ", but got: " + errorResponse.getError());
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(path, errorResponse.getPath());
    }

    @Test
    void handleSalesNotFoundException() {
        // Given
        String message = "Sales not found";
        SalesNotFoundException salesNotFoundException = new SalesNotFoundException(message);

        // When
        String path = "/test";
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(path);
        ResponseEntity<Object> response = this.globalExceptionHandler.handleSalesNotFoundException(salesNotFoundException, request);

        // Then
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertNotNull(errorResponse);
        assertTrue(HttpStatus.NOT_FOUND.getReasonPhrase().equalsIgnoreCase(errorResponse.getError()),
                "Expected error: " + HttpStatus.NOT_FOUND.getReasonPhrase() + ", but got: " + errorResponse.getError());
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(path, errorResponse.getPath());
    }

    @Test
    void handleUnexpectedException() {
        // Given
        String message = "Unexpected error occurred";
        UnexpectedException unexpectedException = new UnexpectedException(message);

        // When
        String path = "/test";
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(path);
        ResponseEntity<Object> response = this.globalExceptionHandler.handleUnexpectedException(unexpectedException, request);

        // Then
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertNotNull(errorResponse);
        assertTrue(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase().equalsIgnoreCase(errorResponse.getError()),
                "Expected error: " + HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase() + ", but got: " + errorResponse.getError());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(path, errorResponse.getPath());
    }

    @Test
    void handleMethodArgumentNotValid() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("sales", "campaign", "error");
        ServletWebRequest servletWebRequest = mock(ServletWebRequest.class);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(servletWebRequest.getRequest()).thenReturn(httpServletRequest);
        when(httpServletRequest.getRequestURI()).thenReturn("/test");

        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

        // When
        ResponseEntity<Object> responseEntity = globalExceptionHandler.handleMethodArgumentNotValid(exception,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, servletWebRequest);

        // Then
        Assertions.assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        Assertions.assertNotNull(errorResponse);
        assertTrue(HttpStatus.BAD_REQUEST.getReasonPhrase().equalsIgnoreCase(errorResponse.getError()),
                "Expected error: " + HttpStatus.BAD_REQUEST.getReasonPhrase() + ", but got: " + errorResponse.getError());
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals("/test", errorResponse.getPath());

        List<ValidationError> validationErrors = errorResponse.getSubErrors();
        assertEquals(1, validationErrors.size());

        ValidationError responseFieldErr = validationErrors.get(0);
        assertEquals(fieldError.getObjectName(), responseFieldErr.getObject());
        assertEquals(fieldError.getField(), responseFieldErr.getField());
        assertEquals(fieldError.getDefaultMessage(), responseFieldErr.getMessage());
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
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase().toLowerCase(), errorResponse.getError().toLowerCase(),
                "Expected error: " + HttpStatus.BAD_REQUEST.getReasonPhrase() + ", but got: " + errorResponse.getError());
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals(path, errorResponse.getPath());
    }
}
