package scripts.risk_premium

import com.demo.bikeinsurance.service.scripts.InsuranceBaseScript
import groovy.transform.BaseScript

@BaseScript InsuranceBaseScript baseScript

def basePremium = getRiskBasePremium("DAMAGE")
def sumInsFactor = getSumInsuredFactor(sumInsured)
def ageFactor = getAgeFactor(make, model, age)

basePremium * sumInsFactor * ageFactor
