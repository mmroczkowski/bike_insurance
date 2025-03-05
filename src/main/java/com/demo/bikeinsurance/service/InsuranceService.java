package com.demo.bikeinsurance.service;

import com.demo.bikeinsurance.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.math.RoundingMode.HALF_UP;

@Service
@RequiredArgsConstructor
public class InsuranceService {

    private final ScriptService scriptService;

    public PolicyResponse calculatePremium(PolicyRequest request) {
        List<ObjectResponse> objects = new ArrayList<>();
        BigDecimal totalPremium = BigDecimal.ZERO;

        for (BicycleRequest bike : request.bicycles()) {
            List<RiskResponse> riskResponses = new ArrayList<>();
            BigDecimal bikePremium = BigDecimal.ZERO;

            for (String riskType : bike.risks()) {
                BigDecimal riskSumInsured = calculateSumInsured(riskType, bike.sumInsured());
                BigDecimal riskPremium = calculateRiskPremium(riskType, riskSumInsured, bike);

                riskResponses.add(new RiskResponse(riskType, round(riskSumInsured), round((riskPremium))));
                bikePremium = bikePremium.add(riskPremium);
            }

            Map<String, String> attributes = Map.of(
                    "MAKE", bike.make(),
                    "MODEL", bike.model(),
                    "MANUFACTURE_YEAR", String.valueOf(bike.manufactureYear())
            );
            objects.add(new ObjectResponse(attributes, bike.coverage(), riskResponses, round(bike.sumInsured()), round(bikePremium)));
            totalPremium = totalPremium.add(bikePremium);
        }

        return new PolicyResponse(objects, round(totalPremium));
    }

    private BigDecimal calculateSumInsured(String riskType, BigDecimal sumInsured) {
        String sumInsuredScriptPath = "insurance_sum/" + riskType + "_SumInsured.groovy";
        return (BigDecimal) scriptService.executeScript(sumInsuredScriptPath, Map.of("sumInsured", sumInsured));
    }

    private BigDecimal calculateRiskPremium(String riskType, BigDecimal riskSumInsured, BicycleRequest bike) {
        String premiumScriptPath = "risk_premium/" + riskType + "_Premium.groovy";
        Map<String, Object> premiumBindings = Map.of(
                "sumInsured", riskSumInsured,
                "make", bike.make(),
                "model", bike.model(),
                "riskCount", bike.risks().size(),
                "age", LocalDate.now().getYear() - bike.manufactureYear()
        );
        return (BigDecimal) scriptService.executeScript(premiumScriptPath, premiumBindings);
    }

    private BigDecimal round(BigDecimal bigDecimal) {
        return bigDecimal.setScale(2, HALF_UP);
    }
}