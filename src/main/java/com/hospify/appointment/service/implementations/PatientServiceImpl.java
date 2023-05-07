package com.hospify.appointment.service.implementations;


import com.hospify.appointment.constants.BloodGroup;
import com.hospify.appointment.constants.Genotype;
import com.hospify.appointment.constants.RoleEnum;
import com.hospify.appointment.dtos.request.EmailDto;
import com.hospify.appointment.dtos.request.PatientRegistrationRequestDto;
import com.hospify.appointment.dtos.response.PatientRegistrationResponse;
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
public class PatientServiceImpl implements PatientService, UserDetailsService {

    private final PatientRepository patientRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final EmailService emailService;
    private final VerificationTokenRepository verificationTokenRepository;

    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final ReentrantLock lock = new ReentrantLock(true);


    @Override
    @Transactional
    public PatientRegistrationResponse registerPatient(PatientRegistrationRequestDto registrationRequestDto) throws IOException {
        lock.lock();
        log.info("register user and create account");
        try {
            if (doesUserAlreadyExist(registrationRequestDto.getEmail())) {
                throw new ResourceAlreadyExistsException(registrationRequestDto.getEmail());
            }
            Patient patient = saveNewPatient(registrationRequestDto);

            sendRegistrationConfirmationEmail(patient, registrationRequestDto.getEmail());
            return buildRegistrationResponse(patient);
        } finally {
            lock.unlock();
        }
    }

    private PatientRegistrationResponse buildRegistrationResponse(Patient patient) {
        return modelMapper.map(patient,PatientRegistrationResponse.class);
    }

    private Patient saveNewPatient(PatientRegistrationRequestDto registrationRequestDto) {
        Patient patient = new Patient();

        patient.setEmail(registrationRequestDto.getEmail());
//        patient.setPatientType(registrationRequestDto);
        patient.setLastName(registrationRequestDto.getLastName());
        patient.setFirstName(registrationRequestDto.getFirstName());
        patient.setPhoneNumber(registrationRequestDto.getPhoneNumber());
        patient.setAddress(registrationRequestDto.getAddress());
        patient.setIsVerified(false);
        patient.setPassword(passwordEncoder.encode(registrationRequestDto.getPassword()));
        patient.setRole(RoleEnum.PATIENT);

         var savedPatient =  patientRepository.save(patient);

         var profileRequest = registrationRequestDto.getPatientProfileRequest();

        PatientProfile patientProfile = PatientProfile.builder()
                .patient(savedPatient)
                .bloodGroup(BloodGroup.valueOf(profileRequest.getBloodGroup()))
                .emergencyContactName(profileRequest.getEmergencyContactName())
                .genotype(Genotype.valueOf(profileRequest.getGenotype()))
                .height(profileRequest.getHeight())
                .weight(profileRequest.getWeight())
                .marriageStatus(profileRequest.getMarriageStatus())
                .emergencyContactPhoneNumber(profileRequest.getEmergencyContactPhoneNumber())
                .emergencyContactName(profileRequest.getEmergencyContactName())
                .build();

        patientProfileRepository.save(patientProfile);

        return  savedPatient;
    }




//    @Override
//    public void updateUser(UpdateUserRequestDto updateUserDto, String id) {
//        log.info("service updateUser - updating user with id :: [{}] ::", id);
//        AppUser user = userRepository.findById(id).<ResourceNotFoundException>orElseThrow(
//                () -> {
//                    throw new ResourceNotFoundException("user does not exist");
//                }
//        );
//        if (StringUtils.isNoneBlank(updateUserDto.getFirstName()))
//            user.setFirstName(updateUserDto.getFirstName());
//        if (StringUtils.isNoneBlank(updateUserDto.getLastName()))
//            user.setLastName(updateUserDto.getLastName());
//        if (StringUtils.isNoneBlank(updateUserDto.getPhoneNumber()))
//            user.setPhoneNumber(updateUserDto.getPhoneNumber());
//
//        userRepository.save(user);
//    }

    @Override
    public String validateVerificationToken(String token) {
        VerificationToken verificationToken =
                verificationTokenRepository.findByToken(token)
                        .orElseThrow(() -> new ResourceNotFoundException("Token does not Exist : "+ token));
        Patient patient = verificationToken.getPatient();
        Calendar cal = Calendar.getInstance();
        // check if user is already verified
        if (patient.getIsVerified()) {
            verificationTokenRepository.delete(verificationToken);
            throw new CustomException("User is already verified", HttpStatus.BAD_REQUEST);
        }
        // check if token is expired
        if((verificationToken.getExpirationTime().getTime() - cal.getTime().getTime()) <= 0){
            throw new CustomException("Token has expired", HttpStatus.BAD_REQUEST);

        }
        // check if token is valid
        if (verificationToken.getExpirationTime().getTime() - cal.getTime().getTime() > 0 ) {
            patient.setIsVerified(true);
            log.info("i have verifed token {}", verificationToken);
            patientRepository.save(patient);

            verificationTokenRepository.delete(verificationToken);
        }
        return "Account verified successfully";
    }



    private boolean doesUserAlreadyExist(String email) {
        return patientRepository.getPatientByEmail(email).isPresent();
    }


    private void sendRegistrationConfirmationEmail(Patient patient, String email) throws IOException {
        String token = generateVerificationToken(patient);
        emailService.sendEmail(EmailDto.builder()
                .sender("noreply@gmail.com")
                .subject("Please Activate Your Account")
                .body("Thank you for Creating your account with us " +
                        "please click on the link below to activate your account : " +
                        "http://localhost:9099/api/v1/account/user/verify-account/" + token)
                .recipient(email)
                .build());
    }

    private String generateVerificationToken(Patient patient) {
        log.info("inside generateVerificationToken, generating token for {}", patient.getEmail());
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setPatient(patient);
        verificationToken.setToken(token);

        log.info("Saving token to database");
        verificationTokenRepository.save(verificationToken);
        return token;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("userService loadUserByUserName - email :: [{}] ::", email);
        Patient patient = patientRepository.getPatientByEmail(email)
                .orElseThrow(
                        () -> {
                            throw new ResourceNotFoundException("user does not exist");
                        }
                );
        Collection<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(patient.getRole().name()));
        return new org.springframework.security.core.userdetails.User(patient.getEmail(), patient.getPassword(), authorities);
    }

}

