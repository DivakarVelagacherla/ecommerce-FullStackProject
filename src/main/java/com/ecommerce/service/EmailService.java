package com.ecommerce.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendOtpEmail(String username, String subject, String body) {

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");
            mimeMessageHelper.setTo(username);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body);
            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new MailSendException("failed to send otp email " + e);
        }
    }
}
