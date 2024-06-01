package com.toyota.authenticationauthorizationservice.utilities.exceptions;

/**
 * This exception is thrown when a user is not found during an authentication or authorization attempt.
 * It extends the {@link RuntimeException}, indicating it is an unchecked exception.
 */

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
