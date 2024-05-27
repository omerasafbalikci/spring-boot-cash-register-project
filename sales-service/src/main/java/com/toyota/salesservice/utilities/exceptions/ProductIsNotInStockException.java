package com.toyota.salesservice.utilities.exceptions;

/**
 * ProductIsNotInStockException thrown if product is out of stock.
 */

public class ProductIsNotInStockException extends RuntimeException {
    public ProductIsNotInStockException(String message) {
        super(message);
    }
}
