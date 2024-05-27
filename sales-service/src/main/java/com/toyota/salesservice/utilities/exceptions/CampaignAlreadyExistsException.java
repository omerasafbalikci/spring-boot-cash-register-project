package com.toyota.salesservice.utilities.exceptions;

/**
 * CampaignAlreadyExistsException thrown if campaign already exists.
 */

public class CampaignAlreadyExistsException extends RuntimeException {
    public CampaignAlreadyExistsException(String message) {
        super(message);
    }
}
