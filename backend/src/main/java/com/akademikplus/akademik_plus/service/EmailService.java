package com.akademikplus.akademik_plus.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@akademik-plus.com}")
    private String fromAddress;

    public void sendPasswordResetEmail(String to, String resetToken) {
        if (mailSender == null) {
            log.warn("Mail sender not configured. Password reset token for {}: {}", to, resetToken);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject("Akademik Plus — Password Reset");
            message.setText("Use the following token to reset your password:\n\n"
                    + resetToken + "\n\nThis token expires in 1 hour.");
            mailSender.send(message);
            log.info("Password reset email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", to, e.getMessage());
        }
    }
}
