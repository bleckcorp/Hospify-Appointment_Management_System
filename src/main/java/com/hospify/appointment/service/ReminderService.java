package com.hospify.appointment.service;

import com.hospify.appointment.constants.ReminderTime;
import com.hospify.appointment.dtos.response.ReminderResponse;
import com.hospify.appointment.entity.Appointment;

public interface ReminderService {

    ReminderResponse makeDefaultReminder(Appointment appointment, ReminderTime reminderTime);

    void sendScheduledReminders();

}
