package com.hospify.appointment.repository;

import com.hospify.appointment.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReminderRepository extends JpaRepository<Reminder, String> {
}