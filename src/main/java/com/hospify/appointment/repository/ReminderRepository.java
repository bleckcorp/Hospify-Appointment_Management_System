package com.hospify.appointment.repository;

import com.hospify.appointment.entity.Appointment;
import com.hospify.appointment.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findAllByAppointment(Appointment appointment);
}