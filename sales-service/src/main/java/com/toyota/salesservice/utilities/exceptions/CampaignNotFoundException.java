package com.toyota.salesservice.utilities.exceptions;

/**
 * CampaignNotFoundException thrown if campaign not found.
 */

public class CampaignNotFoundException extends RuntimeException {
    public CampaignNotFoundException(String message) {
        super(message);
    }
}
