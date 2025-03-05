package com.demo.bikeinsurance.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record ObjectResponse(
        Map<String, String> attributes,
        String coverageType,
        List<RiskResponse> risks,
        BigDecimal sumInsured,
        BigDecimal premium
) {
}