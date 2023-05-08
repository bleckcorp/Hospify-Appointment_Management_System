package com.hospify.appointment.dtos.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospify.appointment.constants.PatientType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class DoctorRegistrationResponse
{

    private String firstName;

    private String lastName;

    private Boolean isVerified;

}