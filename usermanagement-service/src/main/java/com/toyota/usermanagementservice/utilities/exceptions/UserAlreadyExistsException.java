package com.toyota.usermanagementservice.utilities.exceptions;

/**
 * UserAlreadyExistsException thrown if user already exists.
 */

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
