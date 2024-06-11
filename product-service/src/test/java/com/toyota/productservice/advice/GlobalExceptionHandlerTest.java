package com.toyota.productservice.advice;

import com.toyota.productservice.utilities.exceptions.EntityAlreadyExistsException;
import com.toyota.productservice.utilities.exceptions.EntityNotFoundException;
import com.toyota.productservice.utilities.exceptions.ProductIsNotInStockException;
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
    void handleEntityNotFoundException() {
        // Given
        String message = "Entity not found";
        EntityNotFoundException entityNotFoundException = new EntityNotFoundException(message);

        // When
        String path = "/test";
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(path);
        ResponseEntity<Object> response = this.globalExceptionHandler.handleEntityNotFoundException(entityNotFoundException, request);

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
    void handleEntityAlreadyExistsException() {
        // Given
        String message = "Entity already exists";
        EntityAlreadyExistsException entityAlreadyExistsException = new EntityAlreadyExistsException(message);

        // When
        String path = "/test";
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(path);
        ResponseEntity<Object> response = globalExceptionHandler.handleEntityAlreadyExistsException(entityAlreadyExistsException, request);

        // Then
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        Assertions.assertNotNull(errorResponse);
        Assertions.assertTrue(HttpStatus.CONFLICT.getReasonPhrase().equalsIgnoreCase(errorResponse.getError()),
                "Expected error: " + HttpStatus.CONFLICT.getReasonPhrase() + ", but got: " + errorResponse.getError());
        Assertions.assertEquals(HttpStatus.CONFLICT.value(), errorResponse.getStatus());
        Assertions.assertEquals(message, errorResponse.getMessage());
        Assertions.assertEquals(path, errorResponse.getPath());
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
        ResponseEntity<Object> response = globalExceptionHandler.handleProductIsNotInStockException(productIsNotInStockException, request);

        // Then
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        Assertions.assertNotNull(errorResponse);
        Assertions.assertTrue(HttpStatus.BAD_REQUEST.getReasonPhrase().equalsIgnoreCase(errorResponse.getError()),
                "Expected error: " + HttpStatus.BAD_REQUEST.getReasonPhrase() + ", but got: " + errorResponse.getError());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        Assertions.assertEquals(message, errorResponse.getMessage());
        Assertions.assertEquals(path, errorResponse.getPath());
    }

    @Test
    void handleMethodArgumentNotValid() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("product", "category", "error");
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
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        Assertions.assertNotNull(errorResponse);
        Assertions.assertTrue(HttpStatus.BAD_REQUEST.getReasonPhrase().equalsIgnoreCase(errorResponse.getError()),
                "Expected error: " + HttpStatus.BAD_REQUEST.getReasonPhrase() + ", but got: " + errorResponse.getError());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        Assertions.assertEquals("/test", errorResponse.getPath());

        List<ValidationError> validationErrors = errorResponse.getSubErrors();
        Assertions.assertEquals(1, validationErrors.size());

        ValidationError responseFieldErr = validationErrors.get(0);
        Assertions.assertEquals(fieldError.getObjectName(), responseFieldErr.getObject());
        Assertions.assertEquals(fieldError.getField(), responseFieldErr.getField());
        Assertions.assertEquals(fieldError.getDefaultMessage(), responseFieldErr.getMessage());
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
