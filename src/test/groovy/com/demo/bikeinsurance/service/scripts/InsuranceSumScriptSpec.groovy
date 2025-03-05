package com.demo.bikeinsurance.service.scripts

import com.demo.bikeinsurance.service.ScriptService
import spock.lang.Specification

class InsuranceSumScriptSpec extends Specification {

    def groovyScriptService = new ScriptService()

    def "test theft sum insured script returns sumInsured from binding"() {
        given:
        def bindings = [sumInsured: 1500 as BigDecimal]
        def scriptPath = "insurance_sum/THEFT_SumInsured.groovy"

        when:
        def result = groovyScriptService.executeScript(scriptPath, bindings)

        then:
        result == 1500 as BigDecimal
    }

    def "test damage sum insured script returns half of sumInsured"() {
        given:
        def bindings = [sumInsured: 1000 as BigDecimal]
        def scriptPath = "insurance_sum/DAMAGE_SumInsured.groovy"

        when:
        def result = groovyScriptService.executeScript(scriptPath, bindings)

        then:
        result == 500 as BigDecimal
    }

    def "test third party damage sum insured script always returns 100"() {
        given:
        def bindings = [sumInsured: 999 as BigDecimal]
        def scriptPath = "insurance_sum/THIRD_PARTY_DAMAGE_SumInsured.groovy"

        when:
        def result = groovyScriptService.executeScript(scriptPath, bindings)

        then:
        result == 100 as BigDecimal
    }

    def "test theft sum insured with sumInsured=0"() {
        given:
        def bindings = [sumInsured: 0 as BigDecimal]
        def scriptPath = "insurance_sum/THEFT_SumInsured.groovy"

        when:
        def result = groovyScriptService.executeScript(scriptPath, bindings)

        then:
        result == 0 as BigDecimal
    }

    def "test damage sum insured with sumInsured=1"() {
        given:
        def bindings = [sumInsured: 1 as BigDecimal]
        def scriptPath = "insurance_sum/DAMAGE_SumInsured.groovy"

        when:
        def result = groovyScriptService.executeScript(scriptPath, bindings)

        then:
        result == 0.5 as BigDecimal
    }
}