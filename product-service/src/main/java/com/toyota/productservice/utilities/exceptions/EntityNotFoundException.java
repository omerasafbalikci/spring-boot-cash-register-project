package com.toyota.productservice.utilities.exceptions;

/**
 * EntityNotFoundException thrown if entity not found.
 */

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
