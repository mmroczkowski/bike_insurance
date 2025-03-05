package scripts.risk_premium

import com.demo.bikeinsurance.service.scripts.InsuranceBaseScript
import groovy.transform.BaseScript

@BaseScript InsuranceBaseScript baseScript

def basePremium = getRiskBasePremium("THEFT")
def sumInsFactor = getSumInsuredFactor(sumInsured)

basePremium * sumInsFactor
