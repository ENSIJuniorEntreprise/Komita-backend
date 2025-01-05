package com.yt.backend.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void send(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public void sendResetPasswordEmail(String to, String resetLink) {
        String subject = "Password Reset Request";
        String body = String.format(
            "Hello,\n\nWe received a request to reset your password. " +
            "You can reset your password by clicking on the link below:\n\n%s\n\n" +
            "If you did not request this, please ignore this email.\n\nThank you.", resetLink
        );
        send(to, subject, body);
    }
}
