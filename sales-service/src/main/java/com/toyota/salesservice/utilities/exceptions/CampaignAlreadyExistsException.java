package com.toyota.salesservice.utilities.exceptions;

public class CampaignAlreadyExistsException extends RuntimeException {
    public CampaignAlreadyExistsException(String message) {
        super(message);
    }
}
