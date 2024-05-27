package com.toyota.salesservice.utilities.exceptions;

/**
 * Exception thrown when there is an error fetching the inventory response.
 */

public class FetchInventoryResponseException extends RuntimeException {
    public FetchInventoryResponseException(String message) {
        super(message);
    }
}
