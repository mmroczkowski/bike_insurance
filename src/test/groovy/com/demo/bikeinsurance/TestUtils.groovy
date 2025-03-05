package com.demo.bikeinsurance

class TestUtils {
    static def isCloseTo(BigDecimal actual, BigDecimal expected) {
        return actual.subtract(expected).abs() < new BigDecimal("0.01")
    }
}
