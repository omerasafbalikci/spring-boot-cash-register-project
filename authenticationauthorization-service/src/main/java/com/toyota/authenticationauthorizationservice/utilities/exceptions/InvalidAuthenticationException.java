package com.toyota.authenticationauthorizationservice.utilities.exceptions;

/**
 * This exception is thrown when an authentication attempt is invalid.
 * It extends the {@link RuntimeException}, indicating it is an unchecked exception.
 */

public class InvalidAuthenticationException extends RuntimeException {
    public InvalidAuthenticationException(String message) {
        super(message);
    }
}
