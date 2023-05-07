package com.hospify.appointment.dtos.request;

import com.hospify.appointment.constants.ReminderTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentRequest {
    //you can add more fields here
    private ReminderTime reminderTime;
}
