package com.demo.bikeinsurance.dto;

import java.math.BigDecimal;

public record RiskResponse(
        String riskType,
        BigDecimal sumInsured,
        BigDecimal premium
) {
}