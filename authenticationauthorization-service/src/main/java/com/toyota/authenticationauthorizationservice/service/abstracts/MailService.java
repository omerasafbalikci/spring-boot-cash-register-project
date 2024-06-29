package com.toyota.authenticationauthorizationservice.service.abstracts;

/**
 * Interface for mail service class.
 */

public interface MailService {
    /**
     * Sends an email with the given details.
     *
     * @param to the recipient's email address
     * @param subject the subject of the email
     * @param text the text content of the email
     */
    void sendEmail(String to, String subject, String text);
}
