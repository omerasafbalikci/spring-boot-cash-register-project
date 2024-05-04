package com.toyota.salesservice.utilities.exceptions;

public class SalesItemsNotFoundException extends RuntimeException {
    public SalesItemsNotFoundException(String message) {
        super(message);
    }
}
