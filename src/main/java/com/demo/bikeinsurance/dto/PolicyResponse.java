package com.demo.bikeinsurance.dto;

import java.math.BigDecimal;
import java.util.List;

public record PolicyResponse(
        List<ObjectResponse> objects,
        BigDecimal premium
) {
}