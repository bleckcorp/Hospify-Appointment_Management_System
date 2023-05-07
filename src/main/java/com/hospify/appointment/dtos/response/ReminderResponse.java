package com.hospify.appointment.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ReminderResponse {
    private String message;
    private String notificationDateTime;
    private Long appointmentId;
    private String reminderStatus;
}
