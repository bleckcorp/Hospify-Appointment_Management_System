package com.hospify.appointment.repository;

import com.hospify.appointment.entity.ContactInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactInformationRepository extends JpaRepository<ContactInformation, Long> {
}