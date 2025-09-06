package com.leo.bns;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import com.twilio.Twilio;
import com.twilio.exception.TwilioException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.ValidationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

@Service
public class SendSMS {
    
    private static final Logger logger = LoggerFactory.getLogger(SendSMS.class);
    
    @Autowired
    private Environment env;
    
    private String getAccountSid() {
        String sid = env.getProperty("twilio.account.sid");
        if (sid == null || sid.trim().isEmpty()) {
            throw new RuntimeException("Twilio Account SID not configured");
        }
        return sid;
    }
    
    private String getAuthToken() {
        String token = env.getProperty("twilio.auth.token");
        if (token == null || token.trim().isEmpty()) {
            throw new RuntimeException("Twilio Auth Token not configured");
        }
        return token;
    }
    
    private String getPhoneNumber() {
        String phoneNumber = env.getProperty("twilio.phone.number");
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new RuntimeException("Twilio Phone Number not configured");
        }
        return phoneNumber;
    }
    
    private String getValidationPhone() {
        return env.getProperty("twilio.validation.phone");
    }
    
    private String getCallbackUrl() {
        return env.getProperty("twilio.callback.url");
    }
    
    private void initializeTwilio() {
        try {
            Twilio.init(getAccountSid(), getAuthToken());
            logger.debug("Twilio initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize Twilio", e);
            throw new RuntimeException("Twilio initialization failed", e);
        }
    }

    public void sendSms(String toPhoneNumber, String messageBody) {
        if (toPhoneNumber == null || toPhoneNumber.trim().isEmpty()) {
            logger.error("Cannot send SMS: recipient phone number is empty");
            throw new IllegalArgumentException("Recipient phone number is required");
        }
        
        if (messageBody == null || messageBody.trim().isEmpty()) {
            logger.error("Cannot send SMS: message body is empty");
            throw new IllegalArgumentException("Message body is required");
        }
        
        try {
            initializeTwilio();
            
            Message message = Message.creator(
                            new com.twilio.type.PhoneNumber(toPhoneNumber),
                            new com.twilio.type.PhoneNumber(getPhoneNumber()),
                            messageBody)
                    .create();

            logger.info("SMS sent successfully. Message SID: {}", message.getSid());
        } catch (TwilioException e) {
            logger.error("Twilio error while sending SMS to {}: {}", toPhoneNumber, e.getMessage(), e);
            throw new RuntimeException("Failed to send SMS via Twilio", e);
        } catch (Exception e) {
            logger.error("Unexpected error while sending SMS to {}: {}", toPhoneNumber, e.getMessage(), e);
            throw new RuntimeException("SMS service error", e);
        }
    }

    public void performValidation() {
        String validationPhone = getValidationPhone();
        String callbackUrl = getCallbackUrl();
        
        if (validationPhone == null || validationPhone.trim().isEmpty()) {
            logger.error("Cannot perform validation: validation phone number not configured");
            throw new RuntimeException("Validation phone number not configured");
        }
        
        if (callbackUrl == null || callbackUrl.trim().isEmpty()) {
            logger.error("Cannot perform validation: callback URL not configured");
            throw new RuntimeException("Callback URL not configured");
        }
        
        try {
            initializeTwilio();

            ValidationRequest validationRequest = ValidationRequest.creator(
                            new com.twilio.type.PhoneNumber(validationPhone))
                    .setFriendlyName("Third Party VOIP Number")
                    .setStatusCallback(URI.create(callbackUrl))
                    .create();

            logger.info("Validation request created successfully. Friendly Name: {}", 
                       validationRequest.getFriendlyName());
        } catch (TwilioException e) {
            logger.error("Twilio error during phone validation: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create validation request", e);
        } catch (Exception e) {
            logger.error("Unexpected error during phone validation: {}", e.getMessage(), e);
            throw new RuntimeException("Validation service error", e);
        }
    }
}
