package com.hnb.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AgeGenderValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAgeByGender {
    String message() default "Please check age correclty";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
