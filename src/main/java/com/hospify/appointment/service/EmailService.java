package com.hospify.appointment.service;



import com.hospify.appointment.dtos.request.EmailDto;

import java.io.IOException;

public interface EmailService {

    void sendEmail(EmailDto emailDto) throws IOException;

}