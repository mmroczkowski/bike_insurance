package com.demo.bikeinsurance.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PolicyRequest(
        @NotNull(message = "Bicycles list cannot be null")
        @Size(min = 1, message = "At least one bicycle must be provided")
        @Valid
        List<BicycleRequest> bicycles
) {
}