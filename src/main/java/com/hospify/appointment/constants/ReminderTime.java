package com.hospify.appointment.constants;

import lombok.Getter;

@Getter
public enum ReminderTime {

    FIVE_MINUTES(5),
    TEN_MINUTES(10),
    FIFTEEN_MINUTES(15),
    ONE_HOUR(60),
    TWO_HOURS(120);

    private final int numberOfMinutes;

    ReminderTime(int numberOfMinutes) {
        this.numberOfMinutes = numberOfMinutes;

    }
}
