package com.toyota.apigateway.utilities.exception;

/**
 * Exception thrown when an action is attempted without proper authorization.
 */

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
