package com.toyota.salesservice.utilities.exceptions;

public class PaymentTypeIncorrectEntryException extends RuntimeException {
    public PaymentTypeIncorrectEntryException(String message) {
        super(message);
    }
}
