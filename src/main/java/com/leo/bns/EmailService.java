package com.leo.bns;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Properties;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired
    private Environment env;

    public JavaMailSender createJavaMailSender() {
        logger.debug("Creating JavaMailSender");
        
        String portStr = env.getProperty("MAIL_PORT");
        if (portStr == null) {
            throw new RuntimeException("MAIL_PORT not configured");
        }
        
        int port = Integer.parseInt(portStr);

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(env.getProperty("MAIL_HOST"));
        mailSender.setPort(port);
        mailSender.setUsername(env.getProperty("MAIL_FROM"));
        mailSender.setPassword(env.getProperty("MAIL_PASSWORD"));

        // Enable STARTTLS encryption
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.connectiontimeout", "10000");

        logger.debug("JavaMailSender configured successfully");
        return mailSender;
    }

    public void sendEmailNotification(List<Map<String, String>> upcomingBirthdays) {
        if (upcomingBirthdays == null || upcomingBirthdays.isEmpty()) {
            logger.warn("No upcoming birthdays to send notification for");
            return;
        }

        try {
            String senderEmail = env.getProperty("MAIL_FROM");
            String recipientEmail = env.getProperty("MAIL_TO");
            String organizationName = env.getProperty("organization.name");
            
            if (senderEmail == null || recipientEmail == null) {
                throw new RuntimeException("Email configuration is incomplete");
            }

            JavaMailSender mailSender = createJavaMailSender();

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail);
            message.setTo(recipientEmail);
            message.setSubject("🎂 Upcoming Birthday Notifications - " + (organizationName != null ? organizationName : "Organization"));
            
            String emailContent = buildEmailContent(upcomingBirthdays, organizationName);
            message.setText(emailContent);

            mailSender.send(message);
            logger.info("Email notification sent successfully to {} for {} upcoming birthdays", 
                       recipientEmail, upcomingBirthdays.size());
                       
        } catch (MailException e) {
            logger.error("Failed to send email notification", e);
            throw new RuntimeException("Failed to send email notification", e);
        } catch (Exception e) {
            logger.error("Unexpected error while sending email notification", e);
            throw new RuntimeException("Email service error", e);
        }
    }
    
    private String buildEmailContent(List<Map<String, String>> upcomingBirthdays, String organizationName) {
        StringBuilder emailContent = new StringBuilder();
        String orgName = organizationName != null ? organizationName : "Organization";
        
        emailContent.append("🎉 Upcoming Birthdays in ").append(orgName).append(" 🦁\n\n");
        emailContent.append("Hello Team,\n\n");
        emailContent.append("Get ready to celebrate some special moments! Here are the upcoming birthdays:\n");

        for (int i = 0; i < upcomingBirthdays.size(); i++) {
            Map<String, String> birthdayInfo = upcomingBirthdays.get(i);
            String name = birthdayInfo.getOrDefault("name", "Unknown");
            String position = birthdayInfo.getOrDefault("position", "Unknown");
            String birthday = birthdayInfo.get("birthday");
            
            emailContent.append("\n🎂 ").append(i + 1).append(". ").append(name)
                        .append(" (Position: ").append(position).append(")");
            
            if (birthday != null) {
                emailContent.append(" - Birthday: ").append(birthday);
            }
        }

        emailContent.append("\n\nLet's make these days extra special with our heartfelt wishes and joyous celebrations!\n\n");
        emailContent.append("Best regards,\n").append(orgName);
        emailContent.append("\n\n---\nThis is an automated birthday reminder system.");

        return emailContent.toString();
    }
}
