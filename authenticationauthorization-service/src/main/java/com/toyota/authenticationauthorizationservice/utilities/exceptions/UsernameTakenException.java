package com.toyota.authenticationauthorizationservice.utilities.exceptions;

/**
 * This exception is thrown when an attempt is made to register a username that is already taken.
 * It extends the {@link RuntimeException}, indicating it is an unchecked exception.
 */

public class UsernameTakenException extends RuntimeException {
    public UsernameTakenException(String message) {
        super(message);
    }
}
