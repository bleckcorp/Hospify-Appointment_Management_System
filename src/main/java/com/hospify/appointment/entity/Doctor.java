package com.hospify.appointment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hospify.appointment.constants.RoleEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "doctor")
public class Doctor extends BaseEntity{

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "email", unique = true, length = 100)
    private String email;


    @Column(name = "specialization", length = 100)
    private String specialization;

    @OneToMany(mappedBy = "doctor")
    private List<WorkingHour> workingHours;

    @OneToOne(mappedBy = "doctor", cascade = CascadeType.ALL)
    private ContactInformation contactInformation;

    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    private Boolean isVerified;

    @JsonIgnore
    @Column(name = "password", length = 200)
    private String password;



    @ManyToMany(mappedBy = "doctors")
    private Set<Appointment> appointments = new HashSet<>();
}

