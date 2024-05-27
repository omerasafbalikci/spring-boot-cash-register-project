package com.toyota.salesservice.utilities.exceptions;

/**
 * SalesNotFoundException thrown if sales not found.
 */

public class SalesNotFoundException extends RuntimeException {
    public SalesNotFoundException(String message) {
        super(message);
    }
}
