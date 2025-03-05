package com.demo.bikeinsurance.service.scripts

class InsuranceBaseScript extends Script {

    final String MAKE = 'MAKE'
    final String MODEL = 'MODEL'
    final String VALUE_FROM = 'VALUE_FROM'
    final String VALUE_TO = 'VALUE_TO'
    final String FACTOR_MIN = 'FACTOR_MIN'
    final String FACTOR_MAX = 'FACTOR_MAX'
    final String RISK_TYPE = 'RISK_TYPE'
    final String PREMIUM = 'PREMIUM'

    @Override
    Object run() {
        return null
    }

    // data methods
    ArrayList<LinkedHashMap<String, Serializable>> getAgeFactorData() {
        return [
                [(MAKE): 'Canyon', (MODEL): 'CF 5', (VALUE_FROM): 0, (VALUE_TO): 5, (FACTOR_MIN): 1.5, (FACTOR_MAX): 2],
                [(MAKE): 'Canyon', (MODEL): 'CF 5', (VALUE_FROM): 6, (VALUE_TO): 10, (FACTOR_MIN): 1.2, (FACTOR_MAX): 1.4],
                [(MAKE): 'Canyon', (MODEL): 'CF 5', (VALUE_FROM): 11, (VALUE_TO): 15, (FACTOR_MIN): 0.9, (FACTOR_MAX): 1.1],
                [(MAKE): 'Canyon', (VALUE_FROM): 0, (VALUE_TO): 15, (FACTOR_MIN): 0.95, (FACTOR_MAX): 1.6],
                [(MAKE): 'Whyte', (MODEL): 'T-160 RS', (VALUE_FROM): 0, (VALUE_TO): 4, (FACTOR_MIN): 1.6, (FACTOR_MAX): 2.05],
                [(MAKE): 'Whyte', (MODEL): 'T-160 RS', (VALUE_FROM): 5, (VALUE_TO): 10, (FACTOR_MIN): 1.2, (FACTOR_MAX): 1.5],
                [(MAKE): 'Whyte', (MODEL): 'T-160 RS', (VALUE_FROM): 11, (VALUE_TO): 15, (FACTOR_MIN): 0.9, (FACTOR_MAX): 1.1],
                [(MAKE): 'Whyte', (VALUE_FROM): 0, (VALUE_TO): 15, (FACTOR_MIN): 0.95, (FACTOR_MAX): 1.6],
                [(MAKE): 'Pearl', (MODEL): 'Gravel SL EVO', (VALUE_FROM): 0, (VALUE_TO): 2, (FACTOR_MIN): 2.1, (FACTOR_MAX): 2.5],
                [(MAKE): 'Pearl', (MODEL): 'Gravel SL EVO', (VALUE_FROM): 3, (VALUE_TO): 6, (FACTOR_MIN): 1.5, (FACTOR_MAX): 2],
                [(MAKE): 'Pearl', (MODEL): 'Gravel SL EVO', (VALUE_FROM): 7, (VALUE_TO): 15, (FACTOR_MIN): 0.9, (FACTOR_MAX): 1.4],
                [(MAKE): 'Pearl', (VALUE_FROM): 0, (VALUE_TO): 15, (FACTOR_MIN): 0.99, (FACTOR_MAX): 1.8],
                [(MAKE): 'Krush', (VALUE_FROM): 0, (VALUE_TO): 15, (FACTOR_MIN): 0.93, (FACTOR_MAX): 1.75],
                [(MAKE): 'Megamo', (VALUE_FROM): 0, (VALUE_TO): 15, (FACTOR_MIN): 1.1, (FACTOR_MAX): 2.3],
                [(MAKE): 'Sensa', (VALUE_FROM): 0, (VALUE_TO): 15, (FACTOR_MIN): 0.8, (FACTOR_MAX): 2.5],
                [(VALUE_FROM): 0, (VALUE_TO): 15, (FACTOR_MIN): 1, (FACTOR_MAX): 3]
        ]
    }

    ArrayList<LinkedHashMap<String, Serializable>> getRiskCountFactorData() {
        return [
                [(VALUE_FROM): 0, (VALUE_TO): 1, (FACTOR_MIN): 1.3, (FACTOR_MAX): 1.3],
                [(VALUE_FROM): 2, (VALUE_TO): 3, (FACTOR_MIN): 1.2, (FACTOR_MAX): 1.2],
                [(VALUE_FROM): 4, (VALUE_TO): 5, (FACTOR_MIN): 1.1, (FACTOR_MAX): 1.1],
                [(VALUE_FROM): 6, (VALUE_TO): 10, (FACTOR_MIN): 1, (FACTOR_MAX): 1]
        ]
    }

    ArrayList<LinkedHashMap<String, Serializable>> getSumInsuredFactorData() {
        return [
                [(VALUE_FROM): 100, (VALUE_TO): 1000, (FACTOR_MIN): 0.5, (FACTOR_MAX): 1],
                [(VALUE_FROM): 1001, (VALUE_TO): 3000, (FACTOR_MIN): 1, (FACTOR_MAX): 2],
                [(VALUE_FROM): 3001, (VALUE_TO): 5000, (FACTOR_MIN): 2, (FACTOR_MAX): 3]
        ]
    }

    ArrayList<LinkedHashMap<String, Serializable>> getRiskBasePremiumData() {
        return [
                [(RISK_TYPE): 'DAMAGE', (PREMIUM): 10],
                [(RISK_TYPE): 'THIRD_PARTY_DAMAGE', (PREMIUM): 20],
                [(RISK_TYPE): 'THEFT', (PREMIUM): 30]
        ]
    }

    // common calculation methods
    BigDecimal getRiskBasePremium(String riskType) {
        def row = getRiskBasePremiumData().find { it[RISK_TYPE] == riskType }
        if (!row) {
            throw new IllegalArgumentException("No base premium found for risk type: $riskType")
        }
        row[PREMIUM] as BigDecimal
    }

    BigDecimal getSumInsuredFactor(BigDecimal sumInsured) {
        def row = getSumInsuredFactorData().find { r ->
            sumInsured >= (r[VALUE_FROM] as BigDecimal) && sumInsured <= (r[VALUE_TO] as BigDecimal)
        }
        if (!row) {
            throw new IllegalArgumentException("No sum insured factor range found for sumInsured = $sumInsured")
        }
        calculateFactor(row, sumInsured)
    }

    BigDecimal getRiskCountFactor(int riskCount) {
        def row = getRiskCountFactorData().find { r ->
            riskCount >= (r[VALUE_FROM] as int) && riskCount <= (r[VALUE_TO] as int)
        }
        if (!row) {
            throw new IllegalArgumentException("No risk count factor range found for riskCount = $riskCount")
        }
        calculateFactor(row, riskCount)
    }

    BigDecimal getAgeFactor(String make, String model, int age) {
        def row = getAgeFactorData().find { r ->
            r[MAKE] == make && r[MODEL] == model && age >= (r[VALUE_FROM] as int) && age <= (r[VALUE_TO] as int)
        }
        if (!row) {
            row = getAgeFactorData().find { r ->
                r[MAKE] == make && !r.containsKey(MODEL) && age >= (r[VALUE_FROM] as int) && age <= (r[VALUE_TO] as int)
            }
        }
        if (!row) {
            row = getAgeFactorData().find { r ->
                !r.containsKey(MAKE) && age >= (r[VALUE_FROM] as int) && age <= (r[VALUE_TO] as int)
            }
        }
        if (!row) {
            throw new IllegalArgumentException("No age factor range found for make=$make, model=$model, age=$age")
        }
        calculateFactor(row, age)
    }

    BigDecimal calculateFactor(Map<String, Serializable> row, Number actualValue) {
        def factorMin = row[FACTOR_MIN] as BigDecimal
        def factorMax = row[FACTOR_MAX] as BigDecimal
        def valueFrom = row[VALUE_FROM] as BigDecimal
        def valueTo = row[VALUE_TO] as BigDecimal

        factorMax - (factorMax - factorMin) * (valueTo - actualValue) / (valueTo - valueFrom)
    }
}