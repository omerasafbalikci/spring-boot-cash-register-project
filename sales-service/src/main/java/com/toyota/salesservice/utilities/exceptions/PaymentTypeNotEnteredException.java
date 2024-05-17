package com.toyota.salesservice.utilities.exceptions;

public class PaymentTypeNotEnteredException extends RuntimeException {
    public PaymentTypeNotEnteredException(String message) {
        super(message);
    }
}
