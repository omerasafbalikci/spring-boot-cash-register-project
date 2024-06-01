package com.toyota.apigateway.utilities.exception;

/**
 * Exception thrown when an unexpected error occurs within the application.
 */

public class UnexpectedException extends RuntimeException {
    public UnexpectedException(String message) {
        super(message);
    }
}
