package com.hospify.appointment.utils;

import com.hospify.appointment.dtos.request.SmsDTo;

public class SmsTemplates {
    public static SmsDTo createVerificationSMS(String recipientName, String recipientPhone, String OTP) {

        return SmsDTo.builder()
                .recipient(recipientPhone)
                .message( "Dear "+ recipientName + "\n" + "Your OTP is " + OTP)
                .build();


    }
}
