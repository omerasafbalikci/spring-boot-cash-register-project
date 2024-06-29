package com.toyota.authenticationauthorizationservice.service.concretes;

import com.toyota.authenticationauthorizationservice.service.abstracts.MailService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service implementation for managing mails.
 */

@Service
@AllArgsConstructor
public class MailManager implements MailService {
    @Autowired
    private JavaMailSender mailSender;

    /**
     * Sends an email with the specified recipient, subject, and text content.
     *
     * @param to the recipient's email address
     * @param subject the subject of the email
     * @param text the text content of the email
     */
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("asafmarket.ltd@gmail.com");

        mailSender.send(message);
    }
}
