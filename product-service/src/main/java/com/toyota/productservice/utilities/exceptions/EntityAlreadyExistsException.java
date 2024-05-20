package com.toyota.productservice.utilities.exceptions;

/**
 * EntityAlreadyExistsException thrown if entity already exists.
 */

public class EntityAlreadyExistsException extends RuntimeException {
    public EntityAlreadyExistsException(String message) {
        super(message);
    }
}
