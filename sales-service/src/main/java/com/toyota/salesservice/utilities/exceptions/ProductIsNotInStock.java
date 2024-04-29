package com.toyota.salesservice.utilities.exceptions;

public class ProductIsNotInStock extends RuntimeException {
    public ProductIsNotInStock(String message) {
        super(message);
    }
}
