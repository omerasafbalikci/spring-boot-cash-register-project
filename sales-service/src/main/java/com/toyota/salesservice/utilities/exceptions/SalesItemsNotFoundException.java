package com.toyota.salesservice.utilities.exceptions;

/**
 * SalesItemsNotFoundException thrown if sales item not found.
 */

public class SalesItemsNotFoundException extends RuntimeException {
    public SalesItemsNotFoundException(String message) {
        super(message);
    }
}
