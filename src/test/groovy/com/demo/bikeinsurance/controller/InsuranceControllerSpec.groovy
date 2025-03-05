package com.demo.bikeinsurance.controller

import com.demo.bikeinsurance.dto.*
import com.demo.bikeinsurance.service.InsuranceService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import java.time.LocalDate

import static org.mockito.Mockito.when
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(InsuranceController)
class InsuranceControllerSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    @MockitoBean
    InsuranceService insuranceService

    @Autowired
    ObjectMapper objectMapper

    def "successful premium calculation with valid request"() {
        given: "A valid PolicyRequest with one bicycle"
        def currentYear = LocalDate.now().getYear()
        def manufactureYear = currentYear - 5
        def bicycle = new BicycleRequest("Canyon", "CF 5", "Standard", manufactureYear,
                BigDecimal.valueOf(1000), ['THEFT'])
        def request = new PolicyRequest([bicycle])
        def response = new PolicyResponse([new ObjectResponse(
                ["MAKE": "Canyon", "MODEL": "CF 5", "MANUFACTURE_YEAR": manufactureYear.toString()],
                "Standard",
                [new RiskResponse('THEFT', BigDecimal.valueOf(1000), BigDecimal.valueOf(50))],
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(50)
        )], BigDecimal.valueOf(50))

        and: "Mock the InsuranceService to return the response"
        when(insuranceService.calculatePremium(request)).thenReturn(response)

        when: "POST request is made to /api/v1/calculate"
        def result = mockMvc.perform(post("/api/v1/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))

        then: "Expect 200 OK with the correct response"
        result.andExpect(status().isOk())
                .andExpect(content().json(toJson(response)))
    }

    def "validation fails when bicycles list is null"() {
        given: "A PolicyRequest with null bicycles"
        def request = new PolicyRequest(null)

        when: "POST request is made"
        def result = mockMvc.perform(post("/api/v1/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))

        then: "Expect 400 Bad Request with error message"
        result.andExpect(status().isBadRequest())
    }

    def "validation fails when bicycles list is empty"() {
        given: "A PolicyRequest with an empty bicycles list"
        def request = new PolicyRequest([])

        when: "POST request is made"
        def result = mockMvc.perform(post("/api/v1/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))

        then: "Expect 400 Bad Request with error message"
        result.andExpect(status().isBadRequest())
    }

    def "validation fails when bicycle make is null"() {
        given: "A PolicyRequest with a bicycle having null make"
        def request = new PolicyRequest([new BicycleRequest(null, "CF 5", "Standard",
                2020, BigDecimal.valueOf(1000), ['THEFT'])])

        when: "POST request is made"
        def result = mockMvc.perform(post("/api/v1/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))

        then: "Expect 400 Bad Request with error message"
        result.andExpect(status().isBadRequest())
    }

    def "validation fails when bicycle manufacture year is too old"() {
        given: "A PolicyRequest with a bicycle older than 10 years"
        def currentYear = LocalDate.now().getYear()
        def tooOldYear = currentYear - 11
        def request = new PolicyRequest([new BicycleRequest("Canyon", "CF 5", "Standard",
                tooOldYear, BigDecimal.valueOf(1000), ['THEFT'])])

        when: "POST request is made"
        def result = mockMvc.perform(post("/api/v1/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))

        then: "Expect 400 Bad Request with dynamic error message"
        result.andExpect(status().isBadRequest())
    }

    def "validation fails when bicycle sum insured is negative"() {
        given: "A PolicyRequest with a bicycle having negative sum insured"
        def currentYear = LocalDate.now().getYear()
        def request = new PolicyRequest([new BicycleRequest("Canyon", "CF 5", "Standard",
                currentYear - 5, BigDecimal.valueOf(-100), ['THEFT'])])

        when: "POST request is made"
        def result = mockMvc.perform(post("/api/v1/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))

        then: "Expect 400 Bad Request with error message"
        result.andExpect(status().isBadRequest())
    }

    def "validation fails when bicycle sum insured exceeds maximum"() {
        given: "A PolicyRequest with a bicycle having sum insured exceeding 9999"
        def currentYear = LocalDate.now().getYear()
        def request = new PolicyRequest([new BicycleRequest("Canyon", "CF 5", "Standard",
                currentYear - 5, BigDecimal.valueOf(10000), ['THEFT'])])

        when: "POST request is made"
        def result = mockMvc.perform(post("/api/v1/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))

        then: "Expect 400 Bad Request with error message"
        result.andExpect(status().isBadRequest())
    }

    private String toJson(Object obj) {
        objectMapper.writeValueAsString(obj)
    }
}