package com.toyota.usermanagementservice.utilities.exceptions;

/**
 * RoleAlreadyExistsException thrown if role already exists.
 */

public class RoleAlreadyExistsException extends RuntimeException {
    public RoleAlreadyExistsException(String message) {
        super(message);
    }
}
