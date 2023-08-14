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
import java.util.Map;
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

    public  void sendEmailNotification(List<Map<String, String>> upcomingBirthdays) {

        String senderEmail = (String) env.getProperty("MAIL_FROM");

        JavaMailSender mailSender = createJavaMailSender();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo("youngleadersleoclub@gmail.com");
        message.setSubject("Members Birthday Notification");
        StringBuilder emailContent = new StringBuilder();
        emailContent.append("🎉 Upcoming Birthdays in Takoradi Oil City Leos 🦁");
        emailContent.append("\n\nHello Leos,");
        emailContent.append("\n\nGet ready to celebrate some special moments within our pride. Here are the upcoming birthdays:");

        for (int i = 0; i < upcomingBirthdays.size(); i++) {
            Map<String, String> birthdayInfo = upcomingBirthdays.get(i);
            String name = birthdayInfo.get("name");
            String position = birthdayInfo.get("position");
            int displayPosition = i + 1;  // Adding 1 to start counting from 1 instead of 0
            emailContent.append("\n\n🎂 ").append(displayPosition).append(". ").append(name).append(" (Position: ").append(position).append(")");
        }


        emailContent.append("\n\nLet's make these days extra special with our heartfelt wishes and joyous celebrations!");

        emailContent.append("\n\nBest regards,\nTakoradi Oil City Leos");

        message.setText(emailContent.toString());


        mailSender.send(message);
        System.out.println("Email notification sent successfully to yalleyfred@gmail.com");
    }
}
