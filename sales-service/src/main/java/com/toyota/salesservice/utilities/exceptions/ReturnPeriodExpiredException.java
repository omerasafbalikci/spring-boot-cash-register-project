package com.toyota.salesservice.utilities.exceptions;

/**
 * Exception thrown when an attempt is made to return a product after the return period has expired.
 */

public class ReturnPeriodExpiredException extends RuntimeException {
    public ReturnPeriodExpiredException(String message) {
        super(message);
    }
}
