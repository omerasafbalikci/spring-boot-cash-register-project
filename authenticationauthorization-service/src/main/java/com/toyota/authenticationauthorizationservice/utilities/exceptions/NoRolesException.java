package com.toyota.authenticationauthorizationservice.utilities.exceptions;

/**
 * This exception is thrown when a user does not have any roles assigned during an authorization attempt.
 * It extends the {@link RuntimeException}, indicating it is an unchecked exception.
 */

public class NoRolesException extends RuntimeException {
    public NoRolesException(String message) {
        super(message);
    }
}
