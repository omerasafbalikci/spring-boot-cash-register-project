package com.toyota.salesservice.utilities.exceptions;

/**
 * Exception thrown when no payment type is entered for a required operation.
 */

public class PaymentTypeNotEnteredException extends RuntimeException {
    public PaymentTypeNotEnteredException(String message) {
        super(message);
    }
}
