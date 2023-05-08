package com.hospify.appointment.service;

import com.hospify.appointment.dtos.request.SmsDTo;

public interface SmsService {
    void sendSingleSms(SmsDTo smsDTo);
}
