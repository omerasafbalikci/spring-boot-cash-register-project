package com.toyota.authenticationauthorizationservice.utilities.exceptions;

/**
 * This exception is thrown when an incorrect password is provided during an authentication attempt.
 * It extends the {@link RuntimeException}, indicating it is an unchecked exception.
 */

public class IncorrectPasswordException extends RuntimeException {
    public IncorrectPasswordException(String message) {
        super(message);
    }
}
