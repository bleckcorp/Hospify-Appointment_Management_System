package com.hospify.appointment.service.implementations;

import com.hospify.appointment.constants.BloodGroup;
import com.hospify.appointment.constants.Genotype;
import com.hospify.appointment.constants.RoleEnum;
import com.hospify.appointment.dtos.request.DoctorRegistrationRequestDto;
import com.hospify.appointment.dtos.request.EmailDto;
import com.hospify.appointment.dtos.request.PatientRegistrationRequestDto;
import com.hospify.appointment.dtos.request.SmsDTo;
import com.hospify.appointment.dtos.response.DoctorRegistrationResponse;
import com.hospify.appointment.dtos.response.PatientRegistrationResponse;
import com.hospify.appointment.entity.*;
import com.hospify.appointment.exceptions.CustomException;
import com.hospify.appointment.exceptions.ResourceAlreadyExistsException;
import com.hospify.appointment.exceptions.ResourceNotFoundException;
import com.hospify.appointment.repository.*;
import com.hospify.appointment.service.EmailService;
import com.hospify.appointment.service.SmsService;
import com.hospify.appointment.service.UserService;
import com.hospify.appointment.utils.AppUtil;
import com.hospify.appointment.utils.SmsTemplates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService , UserDetailsService {
    private final AppUserRepository appUserRepository;
    private final ContactInformationRepository contactInformationRepository;


    private final SmsService smsService;

    private final PatientProfileRepository patientProfileRepository;
    private final EmailService emailService;
    private final VerificationTokenRepository verificationTokenRepository;

    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final ReentrantLock lock = new ReentrantLock(true);


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = appUserRepository.findByEmail(email).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return buildUserDetails(user);
    }

    private UserDetails buildUserDetails(AppUser user) {
        String role = user.getClass().getSimpleName(); // Get the role as the class name
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_" + role);
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

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

            String token = generateVerificationToken(patient);
//            sendRegistrationConfirmationEmail(registrationRequestDto.getEmail(), token);
            smsService.sendSingleSms(SmsTemplates.createVerificationSMS(patient.getFirstName(),patient.getPhoneNumber(), token));
            return buildRegistrationResponse(patient);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional
    public DoctorRegistrationResponse registerDoctor(DoctorRegistrationRequestDto registrationRequestDto) throws IOException {
        lock.lock();
        log.info("register user and create account");
        try {
            if (doesUserAlreadyExist((registrationRequestDto.getEmail()))) {
                throw new ResourceAlreadyExistsException(registrationRequestDto.getEmail());
            }
            Doctor doctor = saveNewDoctor(registrationRequestDto);

            String token = generateVerificationToken(doctor);
            sendRegistrationConfirmationEmail(registrationRequestDto.getEmail(), token);
            return buildRegistrationResponse(doctor);
        } finally {
            lock.unlock();
        }
    }


    @Override
    public String validateVerificationToken(String token) {

        VerificationToken verificationToken =
                verificationTokenRepository.findByToken(token)
                        .orElseThrow(() -> new ResourceNotFoundException("Token does not Exist : " + token));

            return validateUserVerificationToken(verificationToken);
    }

    @Override
    public boolean resendNewToken(String channel) throws IOException {
        AppUser user = AppUtil.getCurrentUser();

            if (user.getIsVerified()) {
                throw new CustomException("User is already verified", HttpStatus.BAD_REQUEST);
            }
            String token = generateVerificationToken(user);

            if (channel.equalsIgnoreCase("email")) {
                sendRegistrationConfirmationEmail(user.getEmail(), token);
            } else if (channel.equalsIgnoreCase("sms")) {
                sendRegistrationConfirmationSms(user.getPhoneNumber(), token);
            } else if (channel.equalsIgnoreCase("whatsapp")) {
                sendRegistrationConfirmationWhatsapp(user.getPhoneNumber(), token);
            }
        return true;

        }


    private String validateUserVerificationToken(VerificationToken verificationToken) {
        AppUser appUser = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        // check if user is already verified
        if (appUser.getIsVerified()) {
            verificationTokenRepository.delete(verificationToken);
            throw new CustomException("User is already verified", HttpStatus.BAD_REQUEST);
        }
        // check if token is expired
        if ((verificationToken.getExpirationTime().getTime() - cal.getTime().getTime()) <= 0) {
            throw new CustomException("Token has expired", HttpStatus.BAD_REQUEST);

        }
        // check if token is valid
        if (verificationToken.getExpirationTime().getTime() - cal.getTime().getTime() > 0) {
            appUser.setIsVerified(true);
            log.info("i have verifed token {}", verificationToken);
            appUserRepository.save(appUser);

            verificationTokenRepository.delete(verificationToken);
        }
        return "Account verified successfully";

    }




    private void sendRegistrationConfirmationWhatsapp(String phone, String token) {
    }

    private void sendRegistrationConfirmationSms(String phone, String token) {
    }


    private PatientRegistrationResponse buildRegistrationResponse(Patient patient) {
        return modelMapper.map(patient, PatientRegistrationResponse.class);
    }

    private DoctorRegistrationResponse buildRegistrationResponse(Doctor doctor) {

        return modelMapper.map(doctor, DoctorRegistrationResponse.class);
    }

    private Doctor saveNewDoctor(DoctorRegistrationRequestDto registrationRequestDto) {
        Doctor doctor = new Doctor();
        doctor.setEmail(registrationRequestDto.getEmail());
        doctor.setSpecialization(registrationRequestDto.getSpecialization());
        doctor.setLastName(registrationRequestDto.getLastName());
        doctor.setFirstName(registrationRequestDto.getFirstName());
        doctor.setIsVerified(false);
        doctor.setPassword(passwordEncoder.encode(registrationRequestDto.getPassword()));

        var savedDoctor = appUserRepository.save(doctor);

        var contactInfo = ContactInformation.builder()
                .doctor(savedDoctor)
                .phone(registrationRequestDto.getPhoneNumber())
                .build();


        contactInformationRepository.save(contactInfo);

        return savedDoctor;
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

        var savedPatient = appUserRepository.save(patient);

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

        return savedPatient;
    }



    private boolean doesUserAlreadyExist(String email) {

        return appUserRepository.findByEmail(email).isPresent();
    }

    private void sendRegistrationConfirmationEmail(String email, String token) throws IOException {
        emailService.sendEmail(EmailDto.builder()
                .sender("noreply@gmail.com")
                .subject("Please Activate Your Account")
                .body("Thank you for Creating your account with us " +
                        "please click on the link below to activate your account : " +
                        "http://localhost:9099/api/v1/account/user/verify-account/" + token)
                .recipient(email)
                .build());
    }

    private String generateVerificationToken(AppUser user) {
        Random random = new Random();
        int otp = 100_000 + random.nextInt(900_000);

        String token = String.valueOf(otp);
        VerificationToken verificationToken = new VerificationToken(String.valueOf(otp), user);
        if (verificationToken == null) {
            throw new CustomException("Token is null", HttpStatus.BAD_REQUEST);}
            verificationTokenRepository.save(verificationToken);
            return token;
        }


    }

