package com.hospify.appointment.service;

import com.hospify.appointment.dtos.request.PatientRequestDto;

public interface PatientService {

    void createPatient(PatientRequestDto patientRequest);
}
