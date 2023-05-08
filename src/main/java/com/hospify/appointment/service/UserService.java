package com.hospify.appointment.service;

import com.hospify.appointment.dtos.request.DoctorRegistrationRequestDto;
import com.hospify.appointment.dtos.request.PatientRegistrationRequestDto;
import com.hospify.appointment.dtos.response.DoctorRegistrationResponse;
import com.hospify.appointment.dtos.response.PatientRegistrationResponse;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.Principal;

public interface UserService {
    @Transactional
    PatientRegistrationResponse registerPatient(PatientRegistrationRequestDto registrationRequestDto) throws IOException;

    String validateVerificationToken( String token);

    DoctorRegistrationResponse registerDoctor(DoctorRegistrationRequestDto registrationRequestDto) throws IOException;

    boolean resendNewToken(Principal principal, String channel) throws IOException;
}