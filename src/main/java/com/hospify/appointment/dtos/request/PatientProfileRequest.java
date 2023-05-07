package com.hospify.appointment.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PatientProfileRequest {

    private String bloodGroup;

    private int height;

    private String genotype;

    private double weight;

    private String marriageStatus;

    private String occupation;

    private String emergencyContactName;

    private String emergencyContactPhoneNumber;
}

