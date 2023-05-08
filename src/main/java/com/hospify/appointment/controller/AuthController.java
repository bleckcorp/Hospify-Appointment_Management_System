package com.hospify.appointment.controller;


import com.hospify.appointment.dtos.request.DoctorRegistrationRequestDto;
import com.hospify.appointment.dtos.request.PatientRegistrationRequestDto;
import com.hospify.appointment.dtos.response.AppResponse;
import com.hospify.appointment.dtos.response.DoctorRegistrationResponse;
import com.hospify.appointment.dtos.response.PatientRegistrationResponse;
import com.hospify.appointment.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController {

    private final UserService userService;

    @PostMapping(path = "/register/patient")
    public ResponseEntity<AppResponse<?>> registerPatient(@RequestBody @Valid final PatientRegistrationRequestDto registrationRequestDto) throws IOException {
        log.info("controller register: register patient :: [{}] ::", registrationRequestDto.getEmail());
        PatientRegistrationResponse response = userService.registerPatient(registrationRequestDto);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/auth/register").toUriString());
        return ResponseEntity.created(uri).body(AppResponse.buildSuccess(response));
    }

    @PostMapping(path = "/register/doctor")
    public ResponseEntity<AppResponse<?>> registerDoctor(@RequestBody @Valid final DoctorRegistrationRequestDto registrationRequestDto) throws IOException {
        log.info("controller register: register patient :: [{}] ::", registrationRequestDto.getEmail());
        DoctorRegistrationResponse response = userService.registerDoctor(registrationRequestDto);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/auth/register").toUriString());
        return ResponseEntity.created(uri).body(AppResponse.buildSuccess(response));
    }

    @GetMapping("/register/verify")
    public ResponseEntity<AppResponse<Object>> validateRegistrationToken(@RequestParam String token) {
        log.info("controller register: validateRegistrationToken {}", token);

//        token = token.replace("[", "").replace("]","");
        String response = userService.validateVerificationToken(token);

        return response != null ? ResponseEntity.ok().body(AppResponse.buildSuccess(response))
                : ResponseEntity.ok().body(
                AppResponse.<Object>builder()
                        .message("Verification failed")
                        .statusCode(HttpStatus.BAD_REQUEST.value() + "")
                        .build());
    }


    @GetMapping("/resendVerificationToken")
    public ResponseEntity<AppResponse<?>> resendVerificationToken(Principal principal, HttpServletRequest request) throws IOException {

        boolean response = userService.resendNewToken(principal);

        return response ? ResponseEntity.ok().body(AppResponse.buildSuccess("OTP sent to your email"))
                : ResponseEntity.ok().body(
                AppResponse.<Object>builder()
                        .message("Token resend failed")
                        .statusCode(HttpStatus.BAD_REQUEST.value() + "")
                        .build());
    }

}


