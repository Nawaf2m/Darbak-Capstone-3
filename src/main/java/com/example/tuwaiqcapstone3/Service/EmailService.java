package com.example.tuwaiqcapstone3.Service;

import com.example.tuwaiqcapstone3.API.ApiException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    // simple text email (used for the welcome email)
    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            // a failed email must not break registration
            System.out.println("Email failed: " + e.getMessage());
        }
    }

    // HTML email (used for the designed welcome email)
    public void sendHtmlEmail(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (Exception e) {
            // a failed email must not break registration
            System.out.println("Email failed: " + e.getMessage());
        }
    }

    // email with a PDF attachment
    public void sendEmailWithPdf(String to, String subject, String text, byte[] pdf, String fileName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);
            helper.addAttachment(fileName, new ByteArrayResource(pdf));
            mailSender.send(message);
        } catch (Exception e) {
            // here the user asked for the email, so tell him it failed
            throw new ApiException("Failed to send the email: " + e.getMessage());
        }
    }
}