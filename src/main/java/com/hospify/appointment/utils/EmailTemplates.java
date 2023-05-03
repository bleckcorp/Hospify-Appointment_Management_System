package com.hospify.appointment.utils;


import com.hospify.appointment.dtos.request.EmailDto;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class EmailTemplates {

    private static final String senderCredential = "hive@blessingchuks.tech";

    public static EmailDto createVerificationEmail(String recipient, String token, String eventUrl) {
        String verificationUrl = eventUrl
                + "/verifyRegistration?token="
                + token;

        String mailContent = "<p> Dear "+ recipient +", </p>";
        mailContent += "<p> Please click the link below to verify your registration with the link below, </p>";
        mailContent += "<h3><a href=\""+ verificationUrl + "\"> VERIFICATION LINK </a></h3>";
        mailContent += "<p>Thank you <br/> Hive team </p>";


        log.info("Link created {}", verificationUrl);
        return EmailDto.builder()
                .sender(senderCredential)
                .subject("Please Activate Your Account")
                .body(mailContent)
                .recipient(recipient)
                .build();


    }
}
