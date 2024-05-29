package com.toyota.usermanagementservice.advice;

import com.toyota.usermanagementservice.utilities.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

/**
 * Global exception handler for handling application-wide exceptions.
 */

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    /**
     * Handles UserNotFoundException and returns a NOT_FOUND response.
     *
     * @param exception the exception thrown
     * @param request   the HTTP request
     * @return the response entity with error details and NOT_FOUND status
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
        errorResponse.setPath(request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles UnexpectedException and returns an INTERNAL_SERVER_ERROR response.
     *
     * @param exception the exception thrown
     * @param request   the HTTP request
     * @return the response entity with error details and INTERNAL_SERVER_ERROR status
     */
    @ExceptionHandler(UnexpectedException.class)
    public ResponseEntity<Object> handleUnexpectedException(UnexpectedException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        errorResponse.setPath(request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles UserAlreadyExistsException and returns a CONFLICT response.
     *
     * @param exception the exception thrown
     * @param request   the HTTP request
     * @return the response entity with error details and CONFLICT status
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleUserAlreadyExistsException(UserAlreadyExistsException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT, exception.getMessage());
        errorResponse.setPath(request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Handles SingleRoleRemovalException and returns a BAD_REQUEST response.
     *
     * @param exception the exception thrown
     * @param request   the HTTP request
     * @return the response entity with error details and BAD_REQUEST status
     */
    @ExceptionHandler(SingleRoleRemovalException.class)
    public ResponseEntity<Object> handleSingleRoleRemovalException(SingleRoleRemovalException exception, HttpServletRequest request) {
        ErrorResponse errorResponse=new ErrorResponse(HttpStatus.BAD_REQUEST,exception.getMessage());
        errorResponse.setPath(request.getRequestURI());
        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles RoleAlreadyExistsException and returns a CONFLICT response.
     *
     * @param exception the exception thrown
     * @param request   the HTTP request
     * @return the response entity with error details and CONFLICT status
     */
    @ExceptionHandler(RoleAlreadyExistsException.class)
    public ResponseEntity<Object> handleRoleAlreadyExistsException(RoleAlreadyExistsException exception, HttpServletRequest request) {
        ErrorResponse errorResponse=new ErrorResponse(HttpStatus.CONFLICT,exception.getMessage());
        errorResponse.setPath(request.getRequestURI());
        return new ResponseEntity<>(errorResponse,HttpStatus.CONFLICT);
    }

    /**
     * Handles RoleNotFoundException and returns a BAD_REQUEST response.
     *
     * @param exception the exception thrown
     * @param request   the HTTP request
     * @return the response entity with error details and BAD_REQUEST status
     */
    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<Object> handleRoleNotFoundException(RoleNotFoundException exception, HttpServletRequest request) {
        ErrorResponse errorResponse=new ErrorResponse(HttpStatus.BAD_REQUEST,exception.getMessage());
        errorResponse.setPath(request.getRequestURI());
        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles validation exceptions by returning a BAD_REQUEST response.
     *
     * @param exception the exception that was thrown
     * @param headers   the HTTP headers
     * @param status    the HTTP status
     * @param request   the WebRequest
     * @return a ResponseEntity containing an ErrorResponse
     */
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException exception,
                                                               @NonNull HttpHeaders headers,
                                                               @NonNull HttpStatusCode status,
                                                               @NonNull WebRequest request) {
        ServletWebRequest servletWebRequest = (ServletWebRequest) request;
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
        errorResponse.addValidationError(fieldErrors);
        errorResponse.setPath(servletWebRequest.getRequest().getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles HttpMessageNotReadableException by returning a BAD_REQUEST response for malformed JSON requests.
     *
     * @param exception the exception that was thrown
     * @param headers   the HTTP headers
     * @param statusCode the HTTP status code
     * @param request   the WebRequest
     * @return a ResponseEntity containing an ErrorResponse
     */
    @Override
    public ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException exception,
                                                               @NonNull HttpHeaders headers,
                                                               @NonNull HttpStatusCode statusCode,
                                                               @NonNull WebRequest request) {
        ServletWebRequest servletWebRequest = (ServletWebRequest) request;
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, "Malformed Json Request", exception);
        errorResponse.setPath(servletWebRequest.getRequest().getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
