package com.hospify.appointment.entity;

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
@Table(name = "medical_history")
public class MedicalHistory extends BaseEntity{
        /// to be provided from another service (patient management service)
        @OneToOne
        @JoinColumn(name = "patient_id")
        private Patient patient;
}
