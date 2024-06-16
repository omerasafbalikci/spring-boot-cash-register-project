package com.toyota.salesservice.utilities.exceptions;

/**
 * Exception thrown when attempting to perform an operation on a campaign that is in an invalid state (false).
 */

public class CampaignStateFalseException extends RuntimeException {
    public CampaignStateFalseException(String message) {
        super(message);
    }
}
