package com.toyota.usermanagementservice.utilities.exceptions;

/**
 * RoleNotFoundException thrown if role not found.
 */

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String message) {
        super(message);
    }
}
