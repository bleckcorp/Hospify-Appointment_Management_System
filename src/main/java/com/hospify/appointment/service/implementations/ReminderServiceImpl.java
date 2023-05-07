package com.hospify.appointment.service.implementations;

import com.hospify.appointment.constants.ReminderStatus;
import com.hospify.appointment.constants.ReminderTime;
import com.hospify.appointment.dtos.response.ReminderResponse;
import com.hospify.appointment.entity.Appointment;
import com.hospify.appointment.entity.Reminder;
import com.hospify.appointment.repository.ReminderRepository;
import com.hospify.appointment.service.ReminderService;
import com.hospify.appointment.utils.ScheduledJobs;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Log4j2
public class ReminderServiceImpl implements ReminderService {
    private final ReminderRepository reminderRepository;
    private final Scheduler scheduler;

    @Override
    public ReminderResponse makeDefaultReminder(Appointment appointment, ReminderTime reminderTime) throws SchedulerException {

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
        scheduleReminder(reminder);

        return  mapToReminderResponseDto(reminder);
    }

    private void scheduleReminder(Reminder reminder) throws SchedulerException {
        // Create a new job detail for the reminder
        JobDetail jobDetail = JobBuilder.newJob(ScheduledJobs.class)
                .withIdentity("reminder_" + reminder.getId(), "reminder_jobs")
                .usingJobData("reminderId", reminder.getId())
                .build();

        // Create a trigger to fire at the specified notification time
        LocalDateTime notificationTime = reminder.getNotificationDateTime();
        ZonedDateTime zonedDateTime = notificationTime.atZone(ZoneId.systemDefault());

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("reminder_" + reminder.getId(), "reminder_triggers")
                .startAt(Date.from(zonedDateTime.toInstant()))
                .build();


        // Schedule the job with the trigger
        scheduler.scheduleJob(jobDetail, trigger);
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
