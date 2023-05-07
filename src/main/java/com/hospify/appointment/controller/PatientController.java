package com.hospify.appointment.controller;

import com.hospify.appointment.dtos.request.PatientRequestDto;
import com.hospify.appointment.dtos.response.AppResponse;
import com.hospify.appointment.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping({"/api/patient"})
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping({"/create"})
        @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AppResponse<?>> createPatient(@RequestBody PatientRequestDto patientRequest) {
        patientService.createPatient(patientRequest);
        return ResponseEntity.ok(AppResponse.builder()
                .message("Patient created successfully")
                .build());
    }
}
