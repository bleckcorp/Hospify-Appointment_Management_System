package com.hospify.appointment.controller;

import com.hospify.appointment.dtos.response.AppResponse;
import com.hospify.appointment.dtos.response.ReminderResponse;
import com.hospify.appointment.repository.PatientRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {
//    private final PatientRepository userRepository;



}
