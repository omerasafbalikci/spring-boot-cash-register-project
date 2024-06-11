package com.toyota.authenticationauthorizationservice.advice;

import com.toyota.authenticationauthorizationservice.utilities.exceptions.*;
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
    void handleUsernameTakenException() {
        // Given
        String message = "Username taken";
        UsernameTakenException usernameTakenException = new UsernameTakenException(message);

        // When
        String path = "/test";
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(path);
        ResponseEntity<Object> response = globalExceptionHandler.handleUsernameTakenException(usernameTakenException, request);

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
    void handleInvalidBearerToken() {
        // Given
        String message = "Bearer not found";
        InvalidBearerToken invalidBearerToken = new InvalidBearerToken(message);

        // When
        String path = "/test";
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(path);
        ResponseEntity<Object> response = this.globalExceptionHandler.handleInvalidBearerToken(invalidBearerToken, request);

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
    void handleNoRolesException() {
        // Given
        String message = "No roles exception";
        NoRolesException noRolesException = new NoRolesException(message);

        // When
        String path = "/test";
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(path);
        ResponseEntity<Object> response = globalExceptionHandler.handleNoRolesException(noRolesException, request);

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
    void handleRoleNotFoundException() {
        // Given
        String message = "Role not found";
        RoleNotFoundException roleNotFoundException = new RoleNotFoundException(message);

        // When
        String path = "/test";
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(path);
        ResponseEntity<Object> response = this.globalExceptionHandler.handleRoleNotFoundException(roleNotFoundException, request);

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
    void handleIncorrectPasswordException() {
        // Given
        String message = "Incorrect password";
        IncorrectPasswordException incorrectPasswordException = new IncorrectPasswordException(message);

        // When
        String path = "/test";
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(path);
        ResponseEntity<Object> response = this.globalExceptionHandler.handleIncorrectPasswordException(incorrectPasswordException, request);

        // Then
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        Assertions.assertNotNull(errorResponse);
        Assertions.assertTrue(HttpStatus.UNAUTHORIZED.getReasonPhrase().equalsIgnoreCase(errorResponse.getError()),
                "Expected error: " + HttpStatus.UNAUTHORIZED.getReasonPhrase() + ", but got: " + errorResponse.getError());
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), errorResponse.getStatus());
        Assertions.assertEquals(message, errorResponse.getMessage());
        Assertions.assertEquals(path, errorResponse.getPath());
    }

    @Test
    void handleInvalidAuthenticationException() {
        // Given
        String message = "Authentication failed! The provided username or password is incorrect.";
        InvalidAuthenticationException invalidAuthenticationException = new InvalidAuthenticationException(message);

        // When
        String path = "/test";
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(path);
        ResponseEntity<Object> response = this.globalExceptionHandler.handleInvalidAuthenticationException(invalidAuthenticationException, request);

        // Then
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        Assertions.assertNotNull(errorResponse);
        Assertions.assertTrue(HttpStatus.UNAUTHORIZED.getReasonPhrase().equalsIgnoreCase(errorResponse.getError()),
                "Expected error: " + HttpStatus.UNAUTHORIZED.getReasonPhrase() + ", but got: " + errorResponse.getError());
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), errorResponse.getStatus());
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
