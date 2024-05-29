package com.toyota.usermanagementservice.advice;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A class representing a validation error.
 * This class is used to encapsulate details about validation errors that occur within the application.
 */

@Data
@AllArgsConstructor
public class ValidationError {
    private String object;
    private String field;
    private Object rejectedValue;
    private String message;

    /**
     * Constructs a ValidationError with the specified object and message.
     *
     * @param object the name of the object where the validation error occurred
     * @param message a brief message describing the validation error
     */
    public ValidationError(String object, String message) {
        this.object = object;
        this.message = message;
    }
}
