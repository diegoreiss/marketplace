package com.springboot.marketplace.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmailSenderService {
    private final JavaMailSender javaMailSender;
    private static final String EMAIL_FROM = System.getenv("EMAIL_USERNAME");

    @Async
    public void sendEmail(String toEmail, String subject, String body) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            helper.setFrom(EMAIL_FROM);
            helper.setTo(toEmail);
            helper.setText(body, true);
            helper.setSubject(subject);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send email");
        }
    }
}
