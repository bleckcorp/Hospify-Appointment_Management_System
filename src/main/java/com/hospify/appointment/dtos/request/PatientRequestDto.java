package com.hospify.appointment.dtos.request;

import com.hospify.appointment.constants.PatientType;
import com.hospify.appointment.entity.Appointment;
import com.hospify.appointment.entity.InsuranceInformation;
import com.hospify.appointment.entity.PatientProfile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatientRequestDto {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private PatientType patientType;
    private PatientProfile patientProfile;
    private InsuranceInformation insuranceInformation;
    private Set<Appointment> appointment = new HashSet<>();
}
