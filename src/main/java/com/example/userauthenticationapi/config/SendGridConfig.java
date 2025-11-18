package com.example.userauthenticationapi.config;

import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SendGridConfig {
    @Value("${spring.sendgrid.api-key}")
    private String apiKey;

    @Value("${spring.sendgrid.template-id}")
    private String templateId;

    @Value("${spring.sendgrid.sender-name}")
    private String senderName;

    public Mail mail() {
        Email from  = new Email(senderName);
        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setTemplateId(templateId);
        return mail;
    }

    @Bean
    public SendGrid apiKey() {
        return new SendGrid(apiKey);
    }
}
