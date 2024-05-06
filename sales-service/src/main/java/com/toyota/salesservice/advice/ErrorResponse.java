package com.toyota.salesservice.advice;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime localDateTime;
    private int status;
    private String error;
    private String message;
    private String debugMessage;
    private String path;
    private List<ValidationError> subErrors;

    public ErrorResponse() {
        this.localDateTime = LocalDateTime.now();
    }

    public ErrorResponse(HttpStatus status, String message) {
        this();
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
    }

    public ErrorResponse(HttpStatus status, String message, Exception exception) {
        this();
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.debugMessage = exception.getLocalizedMessage();
    }

    public void addValidationError(List<FieldError> errors) {
        if (subErrors == null) {
            subErrors = new ArrayList<>();
        }
        for (FieldError error : errors) {
            ValidationError validationError = new ValidationError(error.getObjectName(), error.getDefaultMessage());
            validationError.setField(error.getField());
            validationError.setRejectedValue(error.getRejectedValue());
            subErrors.add(validationError);
        }
    }
}
