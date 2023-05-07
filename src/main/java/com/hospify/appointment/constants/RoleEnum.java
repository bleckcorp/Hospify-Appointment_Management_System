package com.hospify.appointment.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleEnum {
    PATIENT("ROLE_PATIENT"), DOCTOR("ROLE_DOCTOR");
    private final String role;

}
