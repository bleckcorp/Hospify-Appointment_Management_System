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
@Table(name = "patient")
public class Patient extends BaseEntity{

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "email", unique = true, length = 100)
    private String email;

    @Column(name = "phone_number", length = 16)
    private String phoneNumber;

    @Column(name = "address", length = 100)
    private String address;

    private Boolean isVerified;
    @JsonIgnore
    @Column(name = "password", length = 200)
    private String password;

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

    @Enumerated(EnumType.STRING)
    private RoleEnum role;
}
