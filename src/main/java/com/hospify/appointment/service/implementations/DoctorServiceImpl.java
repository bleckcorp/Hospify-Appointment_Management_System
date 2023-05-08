package com.hospify.appointment.service.implementations;
import com.hospify.appointment.dtos.request.PatientRegistrationRequestDto;
import com.hospify.appointment.dtos.response.PatientRegistrationResponse;
import com.hospify.appointment.entity.Doctor;
import com.hospify.appointment.entity.Patient;
import com.hospify.appointment.exceptions.ResourceNotFoundException;
import com.hospify.appointment.repository.DoctorRepository;
import com.hospify.appointment.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService, UserDetailsService {

    private final DoctorRepository doctorRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("userService loadUserByUserName - email :: [{}] ::", email);

        Doctor doctor = doctorRepository.findByEmail(email)
                .orElseThrow(() -> {throw new ResourceNotFoundException("user does not exist");});

        Collection<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(doctor.getRole().name()));
        return new org.springframework.security.core.userdetails.User(doctor.getEmail(), doctor.getPassword(), authorities);
    }

}
