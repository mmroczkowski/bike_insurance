package com.demo.bikeinsurance.exception

import com.demo.bikeinsurance.controller.InsuranceController
import com.demo.bikeinsurance.dto.*
import com.demo.bikeinsurance.service.InsuranceService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.mockito.Mockito.when
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(controllers = [InsuranceController])
class GlobalExceptionHandlerSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    @MockitoBean
    InsuranceService insuranceService

    @Autowired
    ObjectMapper objectMapper

    def "handle MissingScriptException with correct error response"() {
        given: "A valid request triggering MissingScriptException"
        def request = new PolicyRequest([new BicycleRequest("Canyon", "CF 5", "Standard", 2020,
                BigDecimal.valueOf(1000), ['UNSUPPORTED_RISK'])])

        when(insuranceService.calculatePremium(request)).thenThrow(new MissingScriptException("Missing script for risk UNSUPPORTED_RISK"))

        when: "POST request is made"
        def result = mockMvc.perform(post("/api/v1/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))

        then: "Expect 400 Bad Request with correct error message"
        result.andExpect(status().isInternalServerError())
                .andExpect(jsonPath('$.status').value(400))
                .andExpect(jsonPath('$.error').value("Bad Request"))
                .andExpect(jsonPath('$.message').value("Missing script for risk UNSUPPORTED_RISK"))
                .andExpect(jsonPath('$.path').value("/api/v1/calculate"))
    }

    def "handle generic Exception with correct error response"() {
        given: "A valid request triggering a generic exception"
        def request = new PolicyRequest([new BicycleRequest("Canyon", "CF 5", "Standard", 2020,
                BigDecimal.valueOf(1000), ['THEFT'])])

        when(insuranceService.calculatePremium(request)).thenThrow(new RuntimeException("Unexpected error occurred"))

        when: "POST request is made"
        def result = mockMvc.perform(post("/api/v1/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))

        then: "Expect 500 Internal Server Error with correct error message"
        result.andExpect(status().isInternalServerError())
                .andExpect(jsonPath('$.status').value(500))
                .andExpect(jsonPath('$.error').value("Internal Server Error"))
                .andExpect(jsonPath('$.message').value("Unexpected error occurred"))
                .andExpect(jsonPath('$.path').value("/api/v1/calculate"))
    }

    private String toJson(Object obj) {
        objectMapper.writeValueAsString(obj)
    }
}
