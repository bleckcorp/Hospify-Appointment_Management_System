package com.hospify.appointment.dtos.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospify.appointment.utils.validations.PasswordMatch;
import com.hospify.appointment.utils.validations.PhoneNumber;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown=true)
@PasswordMatch(message = "validation.user.password.match")
public class DoctorRegistrationRequestDto {

    @NotBlank(message = "firstName cannot be empty")
    @Pattern(regexp = "[a-zA-Z]*", message = "FirstName can only have letters")
    @Size(message = "FirstName character length cannot be less than 3 and more than 100", min = 3, max = 100)
    private String firstName;

    @NotBlank(message = "Lastname cannot be empty")
    @Pattern(regexp = "[a-zA-Z]*", message = "lastName can only have letters")
    @Size(message = "Lastname character length cannot be less than 3 and more than 100", min = 3, max = 100)
    private String lastName;

    @NotBlank(message = "email cannot be empty")
    @Email(message = "Must be a valid email!")
    private String email;

    @NotBlank(message = "email cannot be empty")
    private String specialization;

    @NotBlank(message = "email cannot be empty")
    @Email(message = "Must be a valid email!")
    private String contactEmail;

    @NotBlank(message = "Password cannot be empty")
    @Size(message = "Password must be greater than 6 and less than 20", min = 6, max = 20)
    private String password;

    @NotBlank(message = "Confirm password cannot be empty")
    @Size(message = "Password must be greater than 6 and less than 20", min = 6, max = 20)
    private String confirmPassword;

    @PhoneNumber
    @NotBlank(message = "email cannot be empty")
    @Size(message = "Phone number character length cannot be less than 11 and more than 16", min = 11, max = 16)
    private String phoneNumber;

}