package com.leo.bns;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import com.twilio.Twilio;
import com.twilio.converter.Promoter;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.twilio.rest.api.v2010.account.ValidationRequest;


import java.net.URI;
import java.math.BigDecimal;
@Service
public class SendSMS {
    @Autowired
    private Environment env;
    
    private String getAccountSid() {
        return env.getProperty("twilio.account.sid");
    }
    
    private String getAuthToken() {
        return env.getProperty("twilio.auth.token");
    }
    
    private String getPhoneNumber() {
        return env.getProperty("twilio.phone.number");
    }
    
    private String getValidationPhone() {
        return env.getProperty("twilio.validation.phone");
    }
    
    private String getCallbackUrl() {
        return env.getProperty("twilio.callback.url");
    }

    public void sendSms(String toPhoneNumber, String messageBody) {
        Twilio.init(getAccountSid(), getAuthToken());
        Message message = Message.creator(
                        new com.twilio.type.PhoneNumber(toPhoneNumber),
                        new com.twilio.type.PhoneNumber(getPhoneNumber()),
                        messageBody)
                .create();

        System.out.println(message.getSid());
    }

    public void performValidation() {
        Twilio.init(getAccountSid(), getAuthToken());

        ValidationRequest validationRequest = ValidationRequest.creator(
                        new com.twilio.type.PhoneNumber(getValidationPhone()))
                .setFriendlyName("Third Party VOIP Number")
                .setStatusCallback(
                        URI.create(getCallbackUrl()))
                .create();

        System.out.println("Validation Request Friendly Name: " + validationRequest.getFriendlyName());
    }
}
