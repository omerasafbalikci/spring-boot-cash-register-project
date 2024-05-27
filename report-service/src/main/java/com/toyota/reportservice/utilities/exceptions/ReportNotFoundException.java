package com.toyota.reportservice.utilities.exceptions;

/**
 * ReportNotFoundException thrown if report not found.
 */

public class ReportNotFoundException extends RuntimeException {
    public ReportNotFoundException(String message) {
        super(message);
    }
}
