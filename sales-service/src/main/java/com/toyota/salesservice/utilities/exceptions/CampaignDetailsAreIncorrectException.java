package com.toyota.salesservice.utilities.exceptions;

/**
 * CampaignDetailsAreIncorrectException thrown if campaign details are incorrect.
 */

public class CampaignDetailsAreIncorrectException extends RuntimeException {
    public CampaignDetailsAreIncorrectException(String message) {
        super(message);
    }
}
