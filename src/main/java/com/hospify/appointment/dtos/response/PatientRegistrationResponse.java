package com.hospify.appointment.dtos.response;


import com.hospify.appointment.constants.PatientType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PatientRegistrationResponse
{

    private String firstName;

    private String lastName;

    private Boolean isVerified;

    private PatientType patientType;

}
