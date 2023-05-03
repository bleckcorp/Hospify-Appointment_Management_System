package com.hospify.appointment.utils;

import com.hospify.appointment.entity.Reminder;
import com.hospify.appointment.repository.ReminderRepository;
import com.hospify.appointment.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ScheduledJobs implements Job {

    private final ReminderRepository reminderRepository;


    private final EmailService emailService;


    private final Scheduler scheduler;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // Get the reminder ID from the job data map
        Long reminderId = context.getJobDetail().getJobDataMap().getLong("reminderId");

        // Get the reminder from the repository
        Optional<Reminder> optionalReminder = reminderRepository.findById(reminderId);
        if (optionalReminder.isPresent()) {
            Reminder reminder = optionalReminder.get();
//            emailService.sendReminderEmail(reminder);
        }
    }

}
