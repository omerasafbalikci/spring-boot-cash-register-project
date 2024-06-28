package com.toyota.salesservice.utilities.exceptions;

/**
 * Exception thrown when an error occurs while applying a campaign discount.
 */

public class CampaignDiscountException extends RuntimeException {
    public CampaignDiscountException(String message) {
        super(message);
    }
}
