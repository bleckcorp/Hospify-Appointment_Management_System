package com.hospify.appointment.service.implementations;


import com.hospify.appointment.constants.BloodGroup;
import com.hospify.appointment.constants.Genotype;
import com.hospify.appointment.constants.RoleEnum;
import com.hospify.appointment.dtos.request.EmailDto;
import com.hospify.appointment.dtos.request.PatientRegistrationRequestDto;
import com.hospify.appointment.dtos.response.PatientRegistrationResponse;
import com.hospify.appointment.entity.Doctor;
import com.hospify.appointment.entity.Patient;
import com.hospify.appointment.entity.PatientProfile;
import com.hospify.appointment.entity.VerificationToken;
import com.hospify.appointment.exceptions.CustomException;
import com.hospify.appointment.exceptions.ResourceAlreadyExistsException;
import com.hospify.appointment.exceptions.ResourceNotFoundException;
import com.hospify.appointment.repository.PatientProfileRepository;
import com.hospify.appointment.repository.PatientRepository;
import com.hospify.appointment.repository.VerificationTokenRepository;
import com.hospify.appointment.service.EmailService;
import com.hospify.appointment.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;


@Slf4j
@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final EmailService emailService;
    private final VerificationTokenRepository verificationTokenRepository;

    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;


}

