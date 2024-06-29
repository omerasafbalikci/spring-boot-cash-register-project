package com.toyota.authenticationauthorizationservice.service.concretes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MailManagerTest {
    @Mock
    private JavaMailSender mailSender;
    private MailManager mailManager;

    @BeforeEach
    void setUp() {
        mailManager = new MailManager(mailSender);
    }

    @Test
    void sendEmail_success() {
        // Given
        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "Test Text";

        // When
        mailManager.sendEmail(to, subject, text);

        // Then
        verify(mailSender).send(any(SimpleMailMessage.class));
        Mockito.verify(mailSender, Mockito.times(1)).send(any(SimpleMailMessage.class));
    }
}
