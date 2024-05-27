package com.toyota.salesservice.utilities.exceptions;

/**
 * Exception thrown when an operation is attempted with insufficient balance.
 */

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
