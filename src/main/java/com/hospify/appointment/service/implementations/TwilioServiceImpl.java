package com.hospify.appointment.service.implementations;

import com.hospify.appointment.dtos.request.SmsDTo;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.hospify.appointment.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TwilioServiceImpl implements SmsService {

        @Value("${twilio.account.sid}")
        public static final String ACCOUNT_SID = "ACe1f8e2b8b90f0c456db39541c3c88f0d";
        @Value("${twilio.auth.token}")
        public static final String AUTH_TOKEN = "9d01035b34b5d3e7803f717b8b98bf58";
        @Value("${twilio.number}")
        public static final String TWILIO_NUMBER = "+14752656111" ;


       @Override
        public void sendSingleSms(SmsDTo smsDTo) {
           log.info("{}{}",ACCOUNT_SID,AUTH_TOKEN);
            try {
                Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

                Message message = Message.creator(
                        new PhoneNumber(smsDTo.getRecipient()),  // To number
                        new PhoneNumber(TWILIO_NUMBER),  // From number
                        smsDTo.getMessage()                // SMS body
                ).create();

                log.info("Message sent successfully: {}", message.getSid());
            } catch (final ApiException e) {
                log.error("Unable to send SMS: {}", e.getMessage());
            }
        }

    }