package com.toyota.usermanagementservice.utilities.exceptions;

/**
 * UserNotFoundException thrown if user not found.
 */

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
