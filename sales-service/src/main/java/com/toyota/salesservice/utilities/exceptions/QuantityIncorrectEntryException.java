package com.toyota.salesservice.utilities.exceptions;

/**
 * Exception thrown when an incorrect quantity is entered for an operation.
 */

public class QuantityIncorrectEntryException extends RuntimeException {
    public QuantityIncorrectEntryException(String message) {
        super(message);
    }
}
