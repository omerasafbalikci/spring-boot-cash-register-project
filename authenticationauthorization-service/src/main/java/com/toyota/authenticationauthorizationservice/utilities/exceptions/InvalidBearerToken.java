package com.toyota.authenticationauthorizationservice.utilities.exceptions;

/**
 * This exception is thrown when an invalid Bearer token is provided during an authentication attempt.
 * It extends the {@link RuntimeException}, indicating it is an unchecked exception.
 */

public class InvalidBearerToken extends RuntimeException {
    public InvalidBearerToken(String message) {
        super(message);
    }
}
