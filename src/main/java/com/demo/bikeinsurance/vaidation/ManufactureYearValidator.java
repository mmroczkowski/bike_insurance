package com.demo.bikeinsurance.vaidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class ManufactureYearValidator implements ConstraintValidator<ValidManufactureYear, Integer> {

    @Override
    public boolean isValid(Integer manufactureYear, ConstraintValidatorContext context) {
        int currentYear = LocalDate.now().getYear();
        int minYear = currentYear - 10;

        if (manufactureYear < minYear) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Manufacture year must be " + minYear + " or later")
                    .addConstraintViolation();
            return false;
        }
        if (manufactureYear > currentYear) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Manufacture year cannot be after " + currentYear)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
