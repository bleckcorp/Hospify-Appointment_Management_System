package com.hospify.appointment.utils.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=PasswordMatchValidator.class)
public @interface PasswordMatch {
    String message() default "Password and Confirm Password do not match.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
