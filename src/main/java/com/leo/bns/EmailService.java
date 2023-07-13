package com.leo.bns;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

@Service
public class EmailService {

    @Autowired
    private  Environment env;

    public JavaMailSender createJavaMailSender() {

        int port = Integer.parseInt((String) env.getProperty("MAIL_PORT"));

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost((String) env.getProperty("MAIL_HOST"));
        mailSender.setPort(port);
        mailSender.setUsername((String) env.getProperty("MAIL_FROM"));
        mailSender.setPassword((String) env.getProperty("MAIL_PASSWORD"));

        // Enable STARTTLS encryption
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }

    public  void sendEmailNotification(List<String> upcomingBirthdays) {

        String senderEmail = (String) env.getProperty("MAIL_FROM");

        JavaMailSender mailSender = createJavaMailSender();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo("yalleyfred@gmail.com");
        message.setSubject("Birthday");
        StringBuilder emailContent = new StringBuilder();
        emailContent.append("Today is someone's birthday");
        emailContent.append("\n\nUpcoming Birthdays:\n");
        for (String name : upcomingBirthdays) {
            emailContent.append("- ").append(name).append("\n");
        }
        message.setText(emailContent.toString());

        mailSender.send(message);
        System.out.println("Email notification sent successfully to yalleyfred@gmail.com");
    }
}
