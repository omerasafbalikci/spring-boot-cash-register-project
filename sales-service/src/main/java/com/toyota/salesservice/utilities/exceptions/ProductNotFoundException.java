package com.toyota.salesservice.utilities.exceptions;

/**
 * ProductNotFoundException thrown if product not found.
 */

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
