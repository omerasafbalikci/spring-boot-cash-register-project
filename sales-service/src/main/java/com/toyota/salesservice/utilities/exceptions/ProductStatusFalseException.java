package com.toyota.salesservice.utilities.exceptions;

/**
 * Exception thrown when a product's status is false, indicating it cannot be used for the intended operation.
 */

public class ProductStatusFalseException extends RuntimeException {
    public ProductStatusFalseException(String message) {
        super(message);
    }
}
