package com.demo.bikeinsurance.service.scripts

import com.demo.bikeinsurance.service.ScriptService
import spock.lang.Specification

import static com.demo.bikeinsurance.TestUtils.isCloseTo

class RiskPremiumScriptSpec extends Specification {

    def groovyScriptService = new ScriptService()

    def "test theft premium script with sumInsured=500"() {
        given:
        def bindings = [sumInsured: 500 as BigDecimal]

        when:
        def result = groovyScriptService.executeScript("risk_premium/THEFT_Premium.groovy", bindings)

        then:
        isCloseTo(result as BigDecimal, 21.66 as BigDecimal)
    }

    def "test damage premium script with sumInsured=2000, make=Canyon, model=CF 5, age=5"() {
        given:
        def bindings = [
                sumInsured: 2000 as BigDecimal,
                make      : "Canyon",
                model     : "CF 5",
                age       : 5
        ]

        when:
        def result = groovyScriptService.executeScript("risk_premium/DAMAGE_Premium.groovy", bindings)

        then:
        isCloseTo(result as BigDecimal, 30 as BigDecimal)
    }

    def "test third party damage premium script with sumInsured=500, riskCount=2"() {
        given:
        def bindings = [
                sumInsured: 500 as BigDecimal,
                riskCount : 2
        ]

        when:
        def result = groovyScriptService.executeScript("risk_premium/THIRD_PARTY_DAMAGE_Premium.groovy", bindings)

        then:
        isCloseTo(result as BigDecimal, 17.33 as BigDecimal)
    }

    def "test theft premium with sumInsured=100"() {
        given:
        def bindings = [sumInsured: 100 as BigDecimal]

        when:
        def result = groovyScriptService.executeScript("risk_premium/THEFT_Premium.groovy", bindings)

        then:
        result == 15 as BigDecimal
    }

    def "test theft premium with sumInsured=5000"() {
        given:
        def bindings = [sumInsured: 5000 as BigDecimal]

        when:
        def result = groovyScriptService.executeScript("risk_premium/THEFT_Premium.groovy", bindings)

        then:
        result == 90 as BigDecimal
    }

    def "test theft premium with missing sumInsured throws exception"() {
        given:
        def bindings = [:]

        when:
        groovyScriptService.executeScript("risk_premium/THEFT_Premium.groovy", bindings)

        then:
        thrown(IllegalArgumentException)
    }

    def "test damage premium with sumInsured=100, make=Pearl, model=Gravel SL EVO, age=2"() {
        given:
        def bindings = [
                sumInsured: 100 as BigDecimal,
                make      : "Pearl",
                model     : "Gravel SL EVO",
                age       : 2
        ]

        when:
        def result = groovyScriptService.executeScript("risk_premium/DAMAGE_Premium.groovy", bindings)

        then:
        result == 12.5 as BigDecimal
    }

    def "test damage premium with sumInsured=5000, make=Krush, model=null, age=10"() {
        given:
        def bindings = [
                sumInsured: 5000 as BigDecimal,
                make      : "Krush",
                model     : null,
                age       : 10
        ]

        when:
        def result = groovyScriptService.executeScript("risk_premium/DAMAGE_Premium.groovy", bindings)

        then:
        isCloseTo(result as BigDecimal, 44.30 as BigDecimal)
    }

    def "test damage premium with missing age throws exception"() {
        given:
        def bindings = [
                sumInsured: 1000 as BigDecimal,
                make      : "Canyon",
                model     : "CF 5"
        ]

        when:
        groovyScriptService.executeScript("risk_premium/DAMAGE_Premium.groovy", bindings)

        then:
        thrown(IllegalArgumentException)
    }

    def "test third party damage premium with sumInsured=100, riskCount=0"() {
        given:
        def bindings = [
                sumInsured: 100 as BigDecimal,
                riskCount : 0
        ]

        when:
        def result = groovyScriptService.executeScript("risk_premium/THIRD_PARTY_DAMAGE_Premium.groovy", bindings)

        then:
        result == 13.00 as BigDecimal
    }

    def "test third party damage premium with sumInsured=5000, riskCount=10"() {
        given:
        def bindings = [
                sumInsured: 5000 as BigDecimal,
                riskCount : 10
        ]

        when:
        def result = groovyScriptService.executeScript("risk_premium/THIRD_PARTY_DAMAGE_Premium.groovy", bindings)

        then:
        result == 60 as BigDecimal
    }

    def "test third party damage premium with missing riskCount throws exception"() {
        given:
        def bindings = [
                sumInsured: 1000 as BigDecimal
        ]

        when:
        groovyScriptService.executeScript("risk_premium/THIRD_PARTY_DAMAGE_Premium.groovy", bindings)

        then:
        thrown(IllegalArgumentException)
    }
}