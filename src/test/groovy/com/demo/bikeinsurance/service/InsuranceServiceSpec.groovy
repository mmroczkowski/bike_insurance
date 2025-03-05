package com.demo.bikeinsurance.service

import com.demo.bikeinsurance.dto.BicycleRequest
import com.demo.bikeinsurance.dto.PolicyRequest
import com.demo.bikeinsurance.dto.PolicyResponse
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate

class InsuranceServiceSpec extends Specification {

    @Subject
    InsuranceService insuranceService

    ScriptService scriptService = Mock(ScriptService)

    def setup() {
        insuranceService = new InsuranceService(scriptService)
    }

    def "calculatePremium for a 3-year-old bicycle with DAMAGE risk"() {
        given: "A 3-year-old bicycle with DAMAGE risk"
        def currentYear = LocalDate.now().getYear()
        def manufactureYear = currentYear - 3
        def bicycle = new BicycleRequest("Pearl", "Gravel SL EVO", "Premium", manufactureYear, BigDecimal.valueOf(2000), ['DAMAGE'])
        def policyRequest = new PolicyRequest([bicycle])

        and: "Mock script executions for DAMAGE"
        def sumInsuredScriptPath = "insurance_sum/DAMAGE_SumInsured.groovy"
        def premiumScriptPath = "risk_premium/DAMAGE_Premium.groovy"
        scriptService.executeScript(sumInsuredScriptPath, { it.sumInsured == BigDecimal.valueOf(2000) }) >> BigDecimal.valueOf(1000)
        scriptService.executeScript(premiumScriptPath, {
            it.sumInsured == BigDecimal.valueOf(1000) &&
                    it.make == "Pearl" &&
                    it.model == "Gravel SL EVO" &&
                    it.age == 3
        }) >> BigDecimal.valueOf(30)

        when: "Calculate premium"
        PolicyResponse response = insuranceService.calculatePremium(policyRequest)

        then: "Verify response"
        response.premium == BigDecimal.valueOf(30)
        response.objects.size() == 1
        with(response.objects[0]) {
            premium == BigDecimal.valueOf(30)
            sumInsured == BigDecimal.valueOf(2000)
            coverageType == "Premium"
            attributes == ["MAKE": "Pearl", "MODEL": "Gravel SL EVO", "MANUFACTURE_YEAR": manufactureYear.toString()]
            risks.size() == 1
            with(risks[0]) {
                riskType == 'DAMAGE'
                sumInsured == BigDecimal.valueOf(1000)
                premium == BigDecimal.valueOf(30)
            }
        }
    }

    def "calculatePremium for a 4-year-old bicycle with multiple risks (THEFT and DAMAGE)"() {
        given: "A 4-year-old bicycle with THEFT and DAMAGE risks"
        def currentYear = LocalDate.now().getYear()
        def manufactureYear = currentYear - 4
        def bicycle = new BicycleRequest("Canyon", "CF 5", "Standard", manufactureYear, BigDecimal.valueOf(1000), ['THEFT', 'DAMAGE'])
        def policyRequest = new PolicyRequest([bicycle])

        and: "Mock script executions for THEFT"
        def theftSumInsuredPath = "insurance_sum/THEFT_SumInsured.groovy"
        def theftPremiumPath = "risk_premium/THEFT_Premium.groovy"
        scriptService.executeScript(theftSumInsuredPath, { it.sumInsured == BigDecimal.valueOf(1000) }) >> BigDecimal.valueOf(1000)
        scriptService.executeScript(theftPremiumPath, { it.sumInsured == BigDecimal.valueOf(1000) }) >> BigDecimal.valueOf(50)

        and: "Mock script executions for DAMAGE"
        def damageSumInsuredPath = "insurance_sum/DAMAGE_SumInsured.groovy"
        def damagePremiumPath = "risk_premium/DAMAGE_Premium.groovy"
        scriptService.executeScript(damageSumInsuredPath, { it.sumInsured == BigDecimal.valueOf(1000) }) >> BigDecimal.valueOf(500)
        scriptService.executeScript(damagePremiumPath, {
            it.sumInsured == BigDecimal.valueOf(500) &&
                    it.make == "Canyon" &&
                    it.model == "CF 5" &&
                    it.age == 4
        }) >> BigDecimal.valueOf(25)

        when: "Calculate premium"
        PolicyResponse response = insuranceService.calculatePremium(policyRequest)

        then: "Verify response"
        response.premium == BigDecimal.valueOf(75)
        response.objects.size() == 1
        with(response.objects[0]) {
            premium == BigDecimal.valueOf(75)
            sumInsured == BigDecimal.valueOf(1000)
            coverageType == "Standard"
            attributes == ["MAKE": "Canyon", "MODEL": "CF 5", "MANUFACTURE_YEAR": manufactureYear.toString()]
            risks.size() == 2
            def theftRisk = risks.find { it.riskType == 'THEFT' }
            with(theftRisk) {
                sumInsured == BigDecimal.valueOf(1000)
                premium == BigDecimal.valueOf(50)
            }
            def damageRisk = risks.find { it.riskType == 'DAMAGE' }
            with(damageRisk) {
                sumInsured == BigDecimal.valueOf(500)
                premium == BigDecimal.valueOf(25)
            }
        }
    }

    def "calculatePremium for multiple bicycles with different ages and risks"() {
        given: "Two bicycles with different ages and risks"
        def currentYear = LocalDate.now().getYear()
        def bike1ManufactureYear = currentYear - 5
        def bike2ManufactureYear = currentYear - 2
        def bike1 = new BicycleRequest("Canyon", "CF 5", "Standard", bike1ManufactureYear, BigDecimal.valueOf(1000), ['THEFT'])
        def bike2 = new BicycleRequest("Pearl", "Gravel SL EVO", "Premium", bike2ManufactureYear, BigDecimal.valueOf(2000), ['DAMAGE'])
        def policyRequest = new PolicyRequest([bike1, bike2])

        and: "Mock script executions for bike1 (THEFT)"
        def theftSumInsuredPath = "insurance_sum/THEFT_SumInsured.groovy"
        def theftPremiumPath = "risk_premium/THEFT_Premium.groovy"
        scriptService.executeScript(theftSumInsuredPath, { it.sumInsured == BigDecimal.valueOf(1000) }) >> BigDecimal.valueOf(1000)
        scriptService.executeScript(theftPremiumPath, { it.sumInsured == BigDecimal.valueOf(1000) }) >> BigDecimal.valueOf(50)

        and: "Mock script executions for bike2 (DAMAGE)"
        def damageSumInsuredPath = "insurance_sum/DAMAGE_SumInsured.groovy"
        def damagePremiumPath = "risk_premium/DAMAGE_Premium.groovy"
        scriptService.executeScript(damageSumInsuredPath, { it.sumInsured == BigDecimal.valueOf(2000) }) >> BigDecimal.valueOf(1000)
        scriptService.executeScript(damagePremiumPath, {
            it.sumInsured == BigDecimal.valueOf(1000) &&
                    it.make == "Pearl" &&
                    it.model == "Gravel SL EVO" &&
                    it.age == 2
        }) >> BigDecimal.valueOf(30)

        when: "Calculate premium"
        PolicyResponse response = insuranceService.calculatePremium(policyRequest)

        then: "Verify response"
        response.premium == BigDecimal.valueOf(80)
        response.objects.size() == 2
        with(response.objects[0]) {
            premium == BigDecimal.valueOf(50)
            sumInsured == BigDecimal.valueOf(1000)
            coverageType == "Standard"
            attributes == ["MAKE": "Canyon", "MODEL": "CF 5", "MANUFACTURE_YEAR": bike1ManufactureYear.toString()]
            risks.size() == 1
            with(risks[0]) {
                riskType == 'THEFT'
                sumInsured == BigDecimal.valueOf(1000)
                premium == BigDecimal.valueOf(50)
            }
        }
        with(response.objects[1]) {
            premium == BigDecimal.valueOf(30)
            sumInsured == BigDecimal.valueOf(2000)
            coverageType == "Premium"
            attributes == ["MAKE": "Pearl", "MODEL": "Gravel SL EVO", "MANUFACTURE_YEAR": bike2ManufactureYear.toString()]
            risks.size() == 1
            with(risks[0]) {
                riskType == 'DAMAGE'
                sumInsured == BigDecimal.valueOf(1000)
                premium == BigDecimal.valueOf(30)
            }
        }
    }

    def "calculatePremium throws exception when script execution fails"() {
        given: "A 5-year-old bicycle with THEFT risk where script fails"
        def currentYear = LocalDate.now().getYear()
        def manufactureYear = currentYear - 5
        def bicycle = new BicycleRequest("Canyon", "CF 5", "Standard", manufactureYear, BigDecimal.valueOf(1000), ['THEFT'])
        def policyRequest = new PolicyRequest([bicycle])

        and: "Mock script to throw an exception"
        def theftSumInsuredPath = "insurance_sum/THEFT_SumInsured.groovy"
        scriptService.executeScript(theftSumInsuredPath, _) >> { throw new RuntimeException("Script execution failed") }

        when: "Calculate premium"
        insuranceService.calculatePremium(policyRequest)

        then: "Exception is thrown"
        thrown(RuntimeException)
    }
}