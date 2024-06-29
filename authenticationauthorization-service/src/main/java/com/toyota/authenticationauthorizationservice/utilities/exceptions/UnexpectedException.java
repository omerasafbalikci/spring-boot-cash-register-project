package com.toyota.authenticationauthorizationservice.utilities.exceptions;

/**
 * Exception thrown when an unexpected error occurs during application execution.
 */

public class UnexpectedException extends RuntimeException {
    public UnexpectedException(String message) {
        super(message);
    }
}
