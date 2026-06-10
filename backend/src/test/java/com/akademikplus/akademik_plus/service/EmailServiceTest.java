package com.akademikplus.akademik_plus.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendPasswordResetEmail_doesNotSend_whenMailSenderNull() {
        ReflectionTestUtils.setField(emailService, "mailSender", null);
        ReflectionTestUtils.setField(emailService, "fromAddress", "noreply@test.com");

        emailService.sendPasswordResetEmail("user@example.com", "reset-token-123");
        // No exception should occur; logs a warning
    }

    @Test
    void sendPasswordResetEmail_sendsEmail_whenMailSenderConfigured() {
        JavaMailSender mockSender = mock(JavaMailSender.class);
        ReflectionTestUtils.setField(emailService, "mailSender", mockSender);
        ReflectionTestUtils.setField(emailService, "fromAddress", "noreply@test.com");

        emailService.sendPasswordResetEmail("user@example.com", "reset-token-123");

        verify(mockSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendRentReminderEmail_doesNotSend_whenMailSenderNull() {
        ReflectionTestUtils.setField(emailService, "mailSender", null);
        ReflectionTestUtils.setField(emailService, "fromAddress", "noreply@test.com");

        emailService.sendRentReminderEmail("student@example.com", "Your rent reminder");
        // No exception should occur
    }

    @Test
    void sendRentReminderEmail_sendsEmail_whenMailSenderConfigured() {
        JavaMailSender mockSender = mock(JavaMailSender.class);
        ReflectionTestUtils.setField(emailService, "mailSender", mockSender);
        ReflectionTestUtils.setField(emailService, "fromAddress", "noreply@test.com");

        emailService.sendRentReminderEmail("student@example.com", "Rent due soon");

        verify(mockSender).send(any(SimpleMailMessage.class));
    }
}
