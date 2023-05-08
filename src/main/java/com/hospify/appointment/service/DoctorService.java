package com.hospify.appointment.service;

import com.hospify.appointment.dtos.request.PatientRegistrationRequestDto;
import com.hospify.appointment.dtos.response.PatientRegistrationResponse;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

public interface DoctorService  {
    @Transactional
    PatientRegistrationResponse registerDoctor(PatientRegistrationRequestDto registrationRequestDto) throws IOException;

    String validateVerificationToken(String token);
}
