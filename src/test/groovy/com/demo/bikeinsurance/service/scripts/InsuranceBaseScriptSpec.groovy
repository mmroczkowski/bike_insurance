package com.demo.bikeinsurance.service.scripts

import spock.lang.Specification
import spock.lang.Unroll

import static com.demo.bikeinsurance.TestUtils.isCloseTo

class InsuranceBaseScriptSpec extends Specification {

    def baseScript = new InsuranceBaseScript()

    @Unroll
    def "test getRiskBasePremium(#riskType) returns #expectedPremium"() {
        when:
        def premium = baseScript.getRiskBasePremium(riskType)

        then:
        premium == expectedPremium as BigDecimal

        where:
        riskType             || expectedPremium
        'THEFT'              || 30
        'DAMAGE'             || 10
        'THIRD_PARTY_DAMAGE' || 20
    }

    @Unroll
    def "test getSumInsuredFactor(#sumInsured) = ~#expectedFactor"() {
        when:
        def factor = baseScript.getSumInsuredFactor(sumInsured as BigDecimal)

        then:
        isCloseTo(factor, expectedFactor as BigDecimal)

        where:
        sumInsured | expectedFactor
        100        | 0.50
        500        | 0.72
        1000       | 1.00
        3000       | 2.00
    }

    def "test getSumInsuredFactor throws exception when out of known ranges"() {
        when:
        baseScript.getSumInsuredFactor(99999 as BigDecimal)

        then:
        thrown(IllegalArgumentException)
    }

    @Unroll
    def "test getRiskCountFactor(#riskCount) = #expectedFactor"() {
        when:
        def factor = baseScript.getRiskCountFactor(riskCount)

        then:
        factor == expectedFactor as BigDecimal

        where:
        riskCount || expectedFactor
        0         || 1.3
        1         || 1.3
        2         || 1.2
        3         || 1.2
        4         || 1.1
        5         || 1.1
        6         || 1.0
        10        || 1.0
    }

    def "test getRiskCountFactor throws exception when out of known range"() {
        when:
        baseScript.getRiskCountFactor(999)

        then:
        thrown(IllegalArgumentException)
    }

    @Unroll
    def "test getAgeFactor(make=#make, model=#model, age=#age) => ~#expectedFactor"() {
        when:
        def factor = baseScript.getAgeFactor(make, model, age)

        then:
        factor == expectedFactor as BigDecimal

        where:
        make     | model           | age || expectedFactor
        "Canyon" | "CF 5"          | 0   || 1.5
        "Canyon" | "CF 5"          | 5   || 2.0
        "Canyon" | "CF 5"          | 10  || 1.4
        "Canyon" | null            | 3   || 1.08
        "Pearl"  | "Gravel SL EVO" | 2   || 2.5
    }

    def "test getAgeFactor throws exception if no matching row found"() {
        when:
        baseScript.getAgeFactor("NonExistent", "ModelX", 16)

        then:
        thrown(IllegalArgumentException)
    }

    @Unroll
    def "test calculateFactor with valueFrom=#valueFrom, valueTo=#valueTo, factorMin=#factorMin, factorMax=#factorMax, actualValue=#actualValue returns ~#expectedFactor"() {
        given:
        def row = [
                'VALUE_FROM': valueFrom,
                'VALUE_TO'  : valueTo,
                'FACTOR_MIN': factorMin,
                'FACTOR_MAX': factorMax
        ]

        when:
        def factor = baseScript.calculateFactor(row, actualValue)

        then:
        isCloseTo(factor, expectedFactor as BigDecimal)

        where:
        valueFrom | valueTo | factorMin | factorMax | actualValue || expectedFactor
        0         | 10      | 1.0       | 2.0       | 0           || 1.0
        0         | 10      | 1.0       | 2.0       | 10          || 2.0
        0         | 10      | 1.0       | 2.0       | 5           || 1.5
        100       | 1000    | 0.5       | 1.0       | 500         || 0.72
        0         | 15      | 0.9       | 1.4       | 7           || 1.13
    }
}