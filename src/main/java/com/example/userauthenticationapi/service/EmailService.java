package com.example.userauthenticationapi.service;

import com.example.userauthenticationapi.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    private void sendVerificationEmail(String to, String subject, String text) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "true");

        try {
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        mailSender.send(message);
    }

    public void createAndSendVerificationEmail(User user) {
        String subject = "Email verification";
        String verificationCode = user.getVerificationCode();

        try {
            String htmlMessage = loadEmailTemplate(
                    verificationCode
            );
            sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String loadEmailTemplate(String verificationCode) throws IOException {
        String template;
        try (InputStream inputStream = getClass()
                .getClassLoader().getResourceAsStream("templates/verification-email-message.html")) {

            if (inputStream == null) {
                throw new FileNotFoundException("Template not found" + "templates/verification-email-message.html");
            }

            template = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }

        return template.replace("{{verificationCode}}", verificationCode);
    }
}
