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
@DiscriminatorValue("DOCTOR")
public class Doctor extends AppUser{


    @Column(name = "specialization", length = 100)
    private String specialization;

    @OneToMany(mappedBy = "doctor")
    private List<WorkingHour> workingHours;

    @OneToOne(mappedBy = "doctor", cascade = CascadeType.ALL)
    private ContactInformation contactInformation;

    @ManyToMany(mappedBy = "doctors")
    private Set<Appointment> appointments = new HashSet<>();
}

