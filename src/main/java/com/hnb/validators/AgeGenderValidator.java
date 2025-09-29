package com.hnb.validators;

import com.hnb.entities.CustomerDetails;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AgeGenderValidator implements ConstraintValidator<ValidAgeByGender, CustomerDetails> {

    @Override
    public boolean isValid(CustomerDetails detail, ConstraintValidatorContext context) {
        if (detail == null) {
            return true;  // nothing to validate
        }

        if (detail.getGender() == null) {
            // gender validation handled by @NotNull, skip here
            return true;
        }
        
        if ("CHILD".equalsIgnoreCase(detail.getGender().name())) {
            // allow any age if gender is CHILD
            return true;
        }

        Integer age = detail.getAge();
        if (age == null) {
            // age required for non-child gender
            return false;
        }

        return age >= 18 && age < 100;
    }
}
