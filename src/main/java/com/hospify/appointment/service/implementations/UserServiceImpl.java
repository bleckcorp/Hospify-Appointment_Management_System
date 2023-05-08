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
import com.hospify.appointment.utils.SmsTemplates;
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
import java.security.Principal;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService , UserDetailsService {
    private final ContactInformationRepository contactInformationRepository;
    private final DoctorRepository doctorRepository;

    private final SmsService smsService;

    private final PatientRepository patientRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final EmailService emailService;
    private final VerificationTokenRepository verificationTokenRepository;

    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final ReentrantLock lock = new ReentrantLock(true);


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("userService loadUserByUserName - email :: [{}] ::", email);

        Patient patient = patientRepository.findByEmail(email).orElse(null);

        if (patient != null) {
            Collection<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(patient.getRole().name()));
            return new org.springframework.security.core.userdetails.User(patient.getEmail(), patient.getPassword(), authorities);
        }
        Doctor doctor = doctorRepository.findByEmail(email).orElse(null);
        if (doctor != null) {
            Collection<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(doctor.getRole().name()));
            return new org.springframework.security.core.userdetails.User(doctor.getEmail(), doctor.getPassword(), authorities);
        }
        throw new UsernameNotFoundException("User not found with email: " + email);

    }

    @Override
    @Transactional
    public PatientRegistrationResponse registerPatient(PatientRegistrationRequestDto registrationRequestDto) throws IOException {
        lock.lock();
        log.info("register user and create account");
        try {
            if (doesPatientAlreadyExist(registrationRequestDto.getEmail())) {
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
            if (doesDoctorAlreadyExist(registrationRequestDto.getEmail())) {
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

        if (verificationToken.getDoctor() != null) {
            return validateDoctorVerificationToken(verificationToken);
        } else if (verificationToken.getPatient() != null) {
            return validatePatientVerificationToken(verificationToken);
        }
        return null;
    }

    @Override
    public boolean resendNewToken(Principal principal, String channel) throws IOException {


        if (principal instanceof Doctor doctor) {

            if (doctor.getIsVerified()) {
                throw new CustomException("User is already verified", HttpStatus.BAD_REQUEST);
            }
            String token = generateVerificationToken(doctor);

            if (channel.equalsIgnoreCase("email")) {
                sendRegistrationConfirmationEmail(doctor.getEmail(), token);
            } else if (channel.equalsIgnoreCase("sms")) {
                sendRegistrationConfirmationSms(doctor.getContactInformation().getPhone(), token);
            } else if (channel.equalsIgnoreCase("whatsapp")) {
                sendRegistrationConfirmationWhatsapp(doctor.getContactInformation().getPhone(), token);
            }
        } else if (principal instanceof Patient patient) {
            if (patient.getIsVerified()) {
                throw new CustomException("User is already verified", HttpStatus.BAD_REQUEST);
            }
            String token = generateVerificationToken(patient);
            sendRegistrationConfirmationEmail(patient.getEmail(), token);
        }

        return true;
    }

    private String validateDoctorVerificationToken(VerificationToken verificationToken) {
        Doctor doctor = verificationToken.getDoctor();
        Calendar cal = Calendar.getInstance();
        // check if user is already verified
        if (doctor.getIsVerified()) {
            verificationTokenRepository.delete(verificationToken);
            throw new CustomException("User is already verified", HttpStatus.BAD_REQUEST);
        }
        // check if token is expired
        if ((verificationToken.getExpirationTime().getTime() - cal.getTime().getTime()) <= 0) {
            throw new CustomException("Token has expired", HttpStatus.BAD_REQUEST);

        }
        // check if token is valid
        if (verificationToken.getExpirationTime().getTime() - cal.getTime().getTime() > 0) {
            doctor.setIsVerified(true);
            log.info("i have verifed token {}", verificationToken);
            doctorRepository.save(doctor);

            verificationTokenRepository.delete(verificationToken);
        }
        return "Account verified successfully";

    }

    private String validatePatientVerificationToken(VerificationToken token) {
        Patient patient = token.getPatient();
        Calendar cal = Calendar.getInstance();
        // check if user is already verified
        if (patient.getIsVerified()) {
            verificationTokenRepository.delete(token);
            throw new CustomException("User is already verified", HttpStatus.BAD_REQUEST);
        }
        // check if token is expired
        if ((token.getExpirationTime().getTime() - cal.getTime().getTime()) <= 0) {
            throw new CustomException("Token has expired", HttpStatus.BAD_REQUEST);

        }
        // check if token is valid
        if (token.getExpirationTime().getTime() - cal.getTime().getTime() > 0) {
            patient.setIsVerified(true);
            log.info("i have verifed token {}", token);
            patientRepository.save(patient);

            verificationTokenRepository.delete(token);
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
        doctor.setRole(RoleEnum.DOCTOR);

        var savedDoctor = doctorRepository.save(doctor);

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
        patient.setRole(RoleEnum.PATIENT);

        var savedPatient = patientRepository.save(patient);

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


    private boolean doesDoctorAlreadyExist(String email) {

        return doctorRepository.findByEmail(email).isPresent();
    }

    private boolean doesPatientAlreadyExist(String email) {

        return patientRepository.findByEmail(email).isPresent();
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

    private String generateVerificationToken(Object user) {
        Random random = new Random();
        int otp = 100_000 + random.nextInt(900_000);

        String token = String.valueOf(otp);
        VerificationToken verificationToken = null;
        if (user instanceof Doctor) {
            verificationToken = new VerificationToken(token, (Doctor) user);
        } else if (user instanceof Patient) {
            verificationToken = new VerificationToken(token, (Patient) user);
        }
        log.info("Saving token to database {}", verificationToken);
        if (verificationToken == null) {
            throw new CustomException("Token is null", HttpStatus.BAD_REQUEST);}
            verificationTokenRepository.save(verificationToken);
            return token;
        }


    }

