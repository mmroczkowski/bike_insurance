package com.demo.bikeinsurance.controller;

import com.demo.bikeinsurance.dto.PolicyRequest;
import com.demo.bikeinsurance.dto.PolicyResponse;
import com.demo.bikeinsurance.service.InsuranceService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class InsuranceController {

    private final InsuranceService insuranceService;

    @PostMapping("/calculate")
    public ResponseEntity<PolicyResponse> calculate(@Valid @RequestBody PolicyRequest request) {
        PolicyResponse response = insuranceService.calculatePremium(request);
        return ResponseEntity.ok(response);
    }
}