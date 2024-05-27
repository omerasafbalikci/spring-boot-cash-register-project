package com.toyota.reportservice.advice;

import com.toyota.reportservice.utilities.exceptions.ReportNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Global exception handler for handling application-wide exceptions.
 */

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    /**
     * Handles ReportNotFoundException by returning a NOT_FOUND response.
     *
     * @param exception the exception that was thrown
     * @param request   the HttpServletRequest containing the request information
     * @return a ResponseEntity containing an ErrorResponse with a NOT_FOUND status
     */
    @ExceptionHandler(ReportNotFoundException.class)
    public ResponseEntity<Object> handleReportNotFoundException(ReportNotFoundException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
        errorResponse.setPath(request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
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
