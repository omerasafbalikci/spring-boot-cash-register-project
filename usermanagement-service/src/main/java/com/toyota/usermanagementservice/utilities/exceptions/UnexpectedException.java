package com.toyota.usermanagementservice.utilities.exceptions;

/**
 * Exception thrown when an unexpected error occurs in the application.
 */

public class UnexpectedException extends RuntimeException {
    public UnexpectedException(String message) {
        super(message);
    }
}
