package com.toyota.usermanagementservice.advice;

import com.toyota.usermanagementservice.utilities.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void handleUserNotFoundException() {
        // Given
        String message = "User not found";
        UserNotFoundException userNotFoundException = new UserNotFoundException(message);

        // When
        String path = "/test";
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(path);
        ResponseEntity<Object> response = this.globalExceptionHandler.handleUserNotFoundException(userNotFoundException, request);

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
    void handleUnexpectedException() {
        // Given
        String message = "Unexpected exception";
        UnexpectedException unexpectedException = new UnexpectedException(message);

        // When
        String path = "/test";
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(path);
        ResponseEntity<Object> response = globalExceptionHandler.handleUnexpectedException(unexpectedException, request);

        // Then
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        Assertions.assertNotNull(errorResponse);
        Assertions.assertTrue(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase().equalsIgnoreCase(errorResponse.getError()),
                "Expected error: " + HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase() + ", but got: " + errorResponse.getError());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus());
        Assertions.assertEquals(message, errorResponse.getMessage());
        Assertions.assertEquals(path, errorResponse.getPath());
    }

    @Test
    void handleUserAlreadyExistsException() {
        // Given
        String message = "User already exists";
        UserAlreadyExistsException userAlreadyExistsException = new UserAlreadyExistsException(message);

        // When
        String path = "/test";
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(path);
        ResponseEntity<Object> response = globalExceptionHandler.handleUserAlreadyExistsException(userAlreadyExistsException, request);

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
    void handleSingleRoleRemovalException() {
        // Given
        String message = "Cannot remove role. User must have at least one role!";
        SingleRoleRemovalException singleRoleRemovalException = new SingleRoleRemovalException(message);

        // When
        String path = "/test";
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(path);
        ResponseEntity<Object> response = globalExceptionHandler.handleSingleRoleRemovalException(singleRoleRemovalException, request);

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
    void handleRoleAlreadyExistsException() {
        // Given
        String message = "Role already exists";
        RoleAlreadyExistsException roleAlreadyExistsException = new RoleAlreadyExistsException(message);

        // When
        String path = "/test";
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(path);
        ResponseEntity<Object> response = globalExceptionHandler.handleRoleAlreadyExistsException(roleAlreadyExistsException, request);

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
    void handleRoleNotFoundException() {
        // Given
        String message = "Role not Found";
        RoleNotFoundException roleNotFoundException = new RoleNotFoundException(message);

        // When
        String path = "/test";
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(path);
        ResponseEntity<Object> response = globalExceptionHandler.handleRoleNotFoundException(roleNotFoundException, request);

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
        FieldError fieldError = new FieldError("user", "username", "error");
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
