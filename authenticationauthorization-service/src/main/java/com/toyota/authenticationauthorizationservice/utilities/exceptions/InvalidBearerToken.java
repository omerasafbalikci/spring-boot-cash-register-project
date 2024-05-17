package com.toyota.authenticationauthorizationservice.utilities.exceptions;

public class InvalidBearerToken extends RuntimeException {
    public InvalidBearerToken(String message) {
        super(message);
    }
}
