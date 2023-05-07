package com.hospify.appointment.service.implementations;

import com.hospify.appointment.dtos.request.PatientRequestDto;
import com.hospify.appointment.entity.Patient;
import com.hospify.appointment.repository.PatientRepository;
import com.hospify.appointment.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;


    @Override
    public void createPatient(PatientRequestDto patientRequest){
        Patient patient = Patient.builder()
                .firstName(patientRequest.getFirstName())
                .lastName(patientRequest.getLastName())
                .email(patientRequest.getEmail())
                .phoneNumber(patientRequest.getPhoneNumber())
                .address(patientRequest.getAddress())
                .patientType(patientRequest.getPatientType())
                .patientProfile(patientRequest.getPatientProfile())
                .insuranceInformation(patientRequest.getInsuranceInformation())
                .appointment(patientRequest.getAppointment())
                .build();

        patientRepository.save(patient);

        try{
            if (patient.getFirstName().isEmpty() || patient.getLastName().isEmpty() || patient.getEmail().isEmpty() || patient.getPhoneNumber().isEmpty() || patient.getAddress().isEmpty()) {
                throw new Exception("Patient information is missing");
            } else if (patient.getAppointment().isEmpty()) {
                throw new Exception ("No appointment found for patient");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        log.info("Patient {} created successfully", patient.getFirstName());
    }


}
