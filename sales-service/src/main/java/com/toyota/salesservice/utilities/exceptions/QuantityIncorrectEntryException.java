package com.toyota.salesservice.utilities.exceptions;

public class QuantityIncorrectEntryException extends RuntimeException {
    public QuantityIncorrectEntryException(String message) {
        super(message);
    }
}
