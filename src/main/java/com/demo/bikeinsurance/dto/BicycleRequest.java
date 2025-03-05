package com.demo.bikeinsurance.dto;

import com.demo.bikeinsurance.vaidation.ValidManufactureYear;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record BicycleRequest(
        @NotNull(message = "Make cannot be null")
        @Size(min = 1, message = "Make must not be empty")
        String make,

        @NotNull(message = "Model cannot be null")
        @Size(min = 1, message = "Model must not be empty")
        String model,

        @NotNull(message = "Coverage cannot be null")
        String coverage,

        @ValidManufactureYear
        int manufactureYear,

        @Min(value = 0, message = "Sum insured must be at least 0")
        @Max(value = 9999, message = "Sum insured cannot exceed 9999")
        BigDecimal sumInsured,

        @NotNull(message = "Risks cannot be null")
        @Size(min = 1, message = "At least one risk must be specified")
        List<String> risks
) {
}