package com.hospify.appointment.service.implementations;

import com.hospify.appointment.constants.ReminderStatus;
import com.hospify.appointment.constants.ReminderTime;
import com.hospify.appointment.dtos.response.ReminderResponse;
import com.hospify.appointment.entity.Appointment;
import com.hospify.appointment.entity.Reminder;
import com.hospify.appointment.repository.ReminderRepository;
import com.hospify.appointment.service.ReminderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Log4j2
public class ReminderServiceImpl implements ReminderService {
    private final ReminderRepository reminderRepository;


    @Override
    public ReminderResponse makeDefaultReminder(Appointment appointment, ReminderTime reminderTime) {

        //calucate the time to send the reminder

        LocalDateTime notificationDateTime = appointment.getAppointmentDateTime().plusMinutes(reminderTime.getNumberOfMinutes());

        //make new reminder for appointment
        Reminder reminder = Reminder.builder()
                .notificationDateTime(notificationDateTime)
                .message("You have an appointment" )
                .appointment(appointment)
                .reminderStatus(ReminderStatus.PENDING)
                .build();
        //save reminder

        reminderRepository.save(reminder);

        return  mapToReminderResponseDto(reminder);
    }


    @Override
    public void sendScheduledReminders() {

    }
    private ReminderResponse mapToReminderResponseDto(Reminder reminder) {
        return ReminderResponse.builder()
                .message(reminder.getMessage())
                .appointmentId(reminder.getAppointment().getId())
                .notificationDateTime(reminder.getNotificationDateTime().toString())
                .reminderStatus(reminder.getReminderStatus().toString())
                .build();
    }



}
