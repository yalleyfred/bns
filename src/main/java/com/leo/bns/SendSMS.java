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
    // Find your Account Sid and Token at twilio.com/console
    @Autowired
    private Environment env;
    public static final String ACCOUNT_SID = "ACc266508ff8ca39c4e7ea53a30e5d1f59";
    public static final String AUTH_TOKEN = "7673e17500518bba6cec190f037286fd";

    public void sendSms(String toPhoneNumber, String messageBody) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message.creator(
                        new com.twilio.type.PhoneNumber(toPhoneNumber),
                        new com.twilio.type.PhoneNumber("+17622488128"),
                        messageBody)
                .create();

        System.out.println(message.getSid());
    }

    public void performValidation() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        ValidationRequest validationRequest = ValidationRequest.creator(
                        new com.twilio.type.PhoneNumber("+233209539770"))
                .setFriendlyName("Third Party VOIP Number")
                .setStatusCallback(
                        URI.create("https://somefunction.twil.io/caller-id-validation-callback"))
                .create();

        System.out.println("Validation Request Friendly Name: " + validationRequest.getFriendlyName());
    }
}
