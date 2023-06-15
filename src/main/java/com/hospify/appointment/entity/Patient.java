package com.hospify.appointment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hospify.appointment.constants.PatientType;
import com.hospify.appointment.constants.RoleEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DiscriminatorValue("PATIENT")
public class Patient extends AppUser{


    @Column(name = "address", length = 100)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private PatientType patientType;

    @OneToOne(mappedBy = "patient", cascade = CascadeType.ALL)
    private MedicalHistory medicalHistory;

    @OneToOne(mappedBy = "patient", cascade = CascadeType.ALL)
    private PatientProfile patientProfile;

    @OneToOne(mappedBy = "patient", cascade = CascadeType.ALL)
    private InsuranceInformation insuranceInformation;

    @ManyToMany(mappedBy = "patients")
    private Set<Appointment> appointment = new HashSet<>();

}
