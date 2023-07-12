package com.leo.bns;

import org.json.simple.JSONObject;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class EmailService {

    public static JSONObject getEnvVariables() {
        JSONObject jsonData = new JSONObject();
        try{
            Properties fileProps = new Properties();
            FileInputStream fileInputStream = new FileInputStream(".env");

            fileProps.load(fileInputStream);

            // Access specific environment variables from the file
            String mailHost = fileProps.getProperty("MAIL_HOST");
            String mailFrom = fileProps.getProperty("MAIL_FROM");
            String mailPassword = fileProps.getProperty("MAIL_PASSWORD");
            String mailPort = fileProps.getProperty("MAIL_PORT");

            // Use the environment variables from the file
            System.out.println(mailPort);

            jsonData.put("MAIL_HOST", mailHost);
            jsonData.put("MAIL_FROM", mailFrom);
            jsonData.put("MAIL_PASSWORD", mailPassword);
            jsonData.put("MAIL_PORT", mailPort);

            return jsonData;
        }catch (IOException e) {
            e.printStackTrace();
        }
        return jsonData;
    }

    public static JavaMailSender createJavaMailSender() {
        JSONObject env = getEnvVariables();
        int port = Integer.parseInt((String) env.get("MAIL_PORT"));

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost((String) env.get("MAIL_HOST"));
        mailSender.setPort(port);
        mailSender.setUsername((String) env.get("MAIL_FROM"));
        mailSender.setPassword((String) env.get("MAIL_PASSWORD"));

        // Enable STARTTLS encryption
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }

    public static void sendEmailNotification(List<String> upcomingBirthdays) {
        JSONObject env = getEnvVariables();
        String senderEmail = (String) env.get("MAIL_FROM");

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
