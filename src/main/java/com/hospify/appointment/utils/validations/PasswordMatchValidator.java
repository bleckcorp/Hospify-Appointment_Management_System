package com.hospify.appointment.utils.validations;

import com.hospify.appointment.dtos.request.PatientRegistrationRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;


@Component
public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, PatientRegistrationRequestDto> {

    @Override
    public void initialize(PasswordMatch p) {

    }

    @Override
    public boolean isValid(PatientRegistrationRequestDto userRegistrationRequestDto, ConstraintValidatorContext constraintValidatorContext) {
        String plainPassword = userRegistrationRequestDto.getPassword();
        String repeatPassword = userRegistrationRequestDto.getConfirmPassword();

        return plainPassword != null && plainPassword.equals(repeatPassword);
    }

}