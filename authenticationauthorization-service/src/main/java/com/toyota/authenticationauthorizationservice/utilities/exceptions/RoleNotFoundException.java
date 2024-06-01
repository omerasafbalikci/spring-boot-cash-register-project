package com.toyota.authenticationauthorizationservice.utilities.exceptions;

/**
 * This exception is thrown when a specific role is not found during an authorization attempt.
 * It extends the {@link RuntimeException}, indicating it is an unchecked exception.
 */

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String message) {
        super(message);
    }
}
