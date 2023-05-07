package com.hospify.appointment.entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Calendar;
import java.util.Date;


@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "verification_tokens")
public class VerificationToken {
    private static final int EXPIRATION_TIME = 10;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;
    private Date expirationTime;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "patient_id",
            updatable = false,
            foreignKey = @ForeignKey(name= "FK_PATIENT_VERIFY_TOKEN")
    )
    private Doctor doctor;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "doctor_id",
            updatable = false,
            foreignKey = @ForeignKey(name= "FK_DOCTOR_VERIFY_TOKEN")
    )
    private Patient patient;
    public VerificationToken(String token) {
        super();
        this.token = token;
        this.expirationTime = calculateExpirationDate(EXPIRATION_TIME);
    }

    public VerificationToken(String token, Patient patient) {
        super();
        this.token = token;
        this.patient = patient;
        this.expirationTime = calculateExpirationDate(EXPIRATION_TIME);
    }
    public VerificationToken(String token, Doctor doctor) {
        super();
        this.token = token;
        this.doctor = doctor;
        this.expirationTime = calculateExpirationDate(EXPIRATION_TIME);
    }
    private Date calculateExpirationDate(int expirationTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, expirationTime);
        return new Date(calendar.getTime().getTime());
    }
}


