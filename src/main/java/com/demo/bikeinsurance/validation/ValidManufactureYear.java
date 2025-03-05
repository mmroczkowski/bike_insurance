package com.demo.bikeinsurance.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ManufactureYearValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidManufactureYear {
    String message() default "Manufacture year must be within the last 10 years";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}