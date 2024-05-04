package com.toyota.salesservice.utilities.exceptions;

public class ReturnPeriodExpiredException extends RuntimeException {
    public ReturnPeriodExpiredException(String message) {
        super(message);
    }
}
