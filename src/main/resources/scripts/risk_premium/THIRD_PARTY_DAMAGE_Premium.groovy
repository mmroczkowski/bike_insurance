package scripts.risk_premium

import com.demo.bikeinsurance.service.scripts.InsuranceBaseScript
import groovy.transform.BaseScript

@BaseScript InsuranceBaseScript baseScript

def basePremium = getRiskBasePremium("THIRD_PARTY_DAMAGE")
def sumInsFactor = getSumInsuredFactor(sumInsured)
def riskCountFactor = getRiskCountFactor(riskCount)

basePremium * sumInsFactor * riskCountFactor
