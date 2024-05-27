package com.toyota.salesservice.utilities.exceptions;

/**
 * Exception thrown when no money is entered for a required operation.
 */

public class NoMoneyEnteredException extends RuntimeException {
    public NoMoneyEnteredException(String message) {
        super(message);
    }
}
