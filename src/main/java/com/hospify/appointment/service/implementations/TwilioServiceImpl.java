package com.hospify.appointment.service.implementations;

import com.hospify.appointment.dtos.request.SmsDTo;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.hospify.appointment.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TwilioServiceImpl implements SmsService {

        @Value("${twilio.account.sid}")
        public static  String ACCOUNT_SID ;
        @Value("${twilio.auth.token}")
        public static  String AUTH_TOKEN ;
        @Value("${twilio.number}")
        public static  String TWILIO_NUMBER;


       @Override
        public void sendSingleSms(SmsDTo smsDTo) {
            try {
                Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

                Message message = Message.creator(
                        new PhoneNumber(smsDTo.getRecipient()),  // To number
                        new PhoneNumber(TWILIO_NUMBER),  // From number
                        smsDTo.getMessage()                // SMS body
                ).create();

                System.out.println(message.getSid());
            } catch (final ApiException e) {
                log.error("Unable to send SMS: {}", e.getMessage());
            }
        }

    }