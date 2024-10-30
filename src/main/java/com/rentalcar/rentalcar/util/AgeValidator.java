package com.rentalcar.rentalcar.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

public class AgeValidator implements ConstraintValidator<ValidAge, LocalDate> {

    @Override
    public void initialize(ValidAge constraintAnnotation) {}

    @Override
    public boolean isValid(LocalDate dob, ConstraintValidatorContext context) {
        if (dob == null) return false; // Ensure date is not empty
        int age = Period.between(dob, LocalDate.now()).getYears();
        return age >= 18 && age <= 80; // Age must be between 18 and 80
    }
}
