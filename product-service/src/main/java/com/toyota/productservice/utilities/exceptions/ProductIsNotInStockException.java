package com.toyota.productservice.utilities.exceptions;

public class ProductIsNotInStockException extends RuntimeException {
    public ProductIsNotInStockException(String message) {
        super(message);
    }
}
