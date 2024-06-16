package com.toyota.salesservice.advice;

import com.toyota.salesservice.utilities.exceptions.*;
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
     * Handles CampaignNotFoundException by returning a NOT_FOUND response.
     *
     * @param exception the exception that was thrown
     * @param request   the HttpServletRequest
     * @return a ResponseEntity containing an ErrorResponse
     */
    @ExceptionHandler(CampaignNotFoundException.class)
    public ResponseEntity<Object> handleCampaignNotFoundException(CampaignNotFoundException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
        errorResponse.setPath(request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles CampaignAlreadyExistsException by returning a CONFLICT response.
     *
     * @param exception the exception that was thrown
     * @param request   the HttpServletRequest
     * @return a ResponseEntity containing an ErrorResponse
     */
    @ExceptionHandler(CampaignAlreadyExistsException.class)
    public ResponseEntity<Object> handleCampaignAlreadyExistsException(CampaignAlreadyExistsException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT, exception.getMessage());
        errorResponse.setPath(request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Handles CampaignDetailsAreIncorrectException by returning a BAD_REQUEST response.
     *
     * @param exception the exception that was thrown
     * @param request   the HttpServletRequest
     * @return a ResponseEntity containing an ErrorResponse
     */
    @ExceptionHandler(CampaignDetailsAreIncorrectException.class)
    public ResponseEntity<Object> handleCampaignDetailsAreIncorrectException(CampaignDetailsAreIncorrectException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
        errorResponse.setPath(request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles CampaignStateFalseException by returning a BAD_REQUEST response.
     *
     * @param exception the exception that was thrown
     * @param request   the HttpServletRequest representing the current request
     * @return a ResponseEntity containing an ErrorResponse with details about the exception
     */
    @ExceptionHandler(CampaignStateFalseException.class)
    public ResponseEntity<Object> handleCampaignStateFalseException(CampaignStateFalseException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
        errorResponse.setPath(request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles FetchInventoryResponseException by returning an INTERNAL_SERVER_ERROR response.
     *
     * @param exception the exception that was thrown
     * @param request   the HttpServletRequest
     * @return a ResponseEntity containing an ErrorResponse
     */
    @ExceptionHandler(FetchInventoryResponseException.class)
    public ResponseEntity<Object> handleFetchInventoryResponseException(FetchInventoryResponseException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        errorResponse.setPath(request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles InsufficientBalanceException by returning a BAD_REQUEST response.
     *
     * @param exception the exception that was thrown
     * @param request   the HttpServletRequest
     * @return a ResponseEntity containing an ErrorResponse
     */
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<Object> handleInsufficientBalanceException(InsufficientBalanceException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
        errorResponse.setPath(request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles NoMoneyEnteredException by returning a BAD_REQUEST response.
     *
     * @param exception the exception that was thrown
     * @param request   the HttpServletRequest
     * @return a ResponseEntity containing an ErrorResponse
     */
    @ExceptionHandler(NoMoneyEnteredException.class)
    public ResponseEntity<Object> handleNoMoneyEnteredException(NoMoneyEnteredException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
        errorResponse.setPath(request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles PaymentTypeNotEnteredException by returning a BAD_REQUEST response.
     *
     * @param exception the exception that was thrown
     * @param request   the HttpServletRequest
     * @return a ResponseEntity containing an ErrorResponse
     */
    @ExceptionHandler(PaymentTypeNotEnteredException.class)
    public ResponseEntity<Object> handlePaymentTypeNotEnteredException(PaymentTypeNotEnteredException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
        errorResponse.setPath(request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles ProductIsNotInStockException by returning a BAD_REQUEST response.
     *
     * @param exception the exception that was thrown
     * @param request   the HttpServletRequest
     * @return a ResponseEntity containing an ErrorResponse
     */
    @ExceptionHandler(ProductIsNotInStockException.class)
    public ResponseEntity<Object> handleProductIsNotInStockException(ProductIsNotInStockException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
        errorResponse.setPath(request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles ProductNotFoundException by returning a NOT_FOUND response.
     *
     * @param exception the exception that was thrown
     * @param request   the HttpServletRequest
     * @return a ResponseEntity containing an ErrorResponse
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Object> handleProductNotFoundException(ProductNotFoundException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
        errorResponse.setPath(request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles ProductStatusFalseException by returning a BAD_REQUEST response.
     *
     * @param exception the exception that was thrown
     * @param request   the HttpServletRequest
     * @return a ResponseEntity containing an ErrorResponse
     */
    @ExceptionHandler(ProductStatusFalseException.class)
    public ResponseEntity<Object> handleProductStatusFalseException(ProductStatusFalseException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
        errorResponse.setPath(request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles QuantityIncorrectEntryException by returning a BAD_REQUEST response.
     *
     * @param exception the exception that was thrown
     * @param request   the HttpServletRequest
     * @return a ResponseEntity containing an ErrorResponse
     */
    @ExceptionHandler(QuantityIncorrectEntryException.class)
    public ResponseEntity<Object> handleQuantityIncorrectEntryException(QuantityIncorrectEntryException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
        errorResponse.setPath(request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles ReturnPeriodExpiredException by returning a BAD_REQUEST response.
     *
     * @param exception the exception that was thrown
     * @param request   the HttpServletRequest
     * @return a ResponseEntity containing an ErrorResponse
     */
    @ExceptionHandler(ReturnPeriodExpiredException.class)
    public ResponseEntity<Object> handleReturnPeriodExpiredException(ReturnPeriodExpiredException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
        errorResponse.setPath(request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles SalesItemsNotFoundException by returning a NOT_FOUND response.
     *
     * @param exception the exception that was thrown
     * @param request   the HttpServletRequest
     * @return a ResponseEntity containing an ErrorResponse
     */
    @ExceptionHandler(SalesItemsNotFoundException.class)
    public ResponseEntity<Object> handleSalesItemsNotFoundException(SalesItemsNotFoundException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
        errorResponse.setPath(request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles SalesNotFoundException by returning a NOT_FOUND response.
     *
     * @param exception the exception that was thrown
     * @param request   the HttpServletRequest
     * @return a ResponseEntity containing an ErrorResponse
     */
    @ExceptionHandler(SalesNotFoundException.class)
    public ResponseEntity<Object> handleSalesNotFoundException(SalesNotFoundException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
        errorResponse.setPath(request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles UnexpectedException by returning an INTERNAL_SERVER_ERROR response.
     *
     * @param exception the exception that was thrown
     * @param request   the HttpServletRequest
     * @return a ResponseEntity containing an ErrorResponse
     */
    @ExceptionHandler(UnexpectedException.class)
    public ResponseEntity<Object> handleUnexpectedException(UnexpectedException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        errorResponse.setPath(request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
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
