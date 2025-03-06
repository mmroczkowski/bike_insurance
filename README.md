# Bicycle Insurance Service

## Overview

This repository provides a simple Spring Boot application for calculating bicycle insurance premiums. It demonstrates:

- Dynamic Groovy script loading for **Sum Insured** and **Premium** calculations per risk type.
- A **base script** (`InsuranceBaseScript`) for shared methods and factor data (e.g., base premium, age factor, sum
  insured factor).
- Use of **Spock** for unit tests, given its concise Groovy syntax and readability.

## Key Implementation Notes

1. **Refactoring the Base Script**
   - The `BaseScript` class name was changed to `InsuranceBaseScript` to avoid naming conflicts with groovy annotation.
   - Typically, existing codebase should be preserved, but in this case, a refactor was necessary to improve code
     quality.

2. **Dynamic Script Loading**
   - Scripts are **divided by risk type** and loaded dynamically at runtime (one for Sum Insured, one for Premium).
   - This allows easy extension or future runtime modifications without touching the core code.

3. **Adding New Risk Types**
   - To incorporate a new risk type, create two Groovy scripts: one for calculating Sum Insured and another for
     Premium.
   - Update the base script data if you need new factor ranges or a different base premium.

4. **Precision and Rounding**
   - Used `BigDecimal` with default precision for calculations and applied **HALF_UP** rounding just before response
     creation.
   - This ensures high precision internally and consistent human-friendly response in API.

5. **Testing**
   - **Spock** is used because of its more expressive syntax in Groovy.
   - Tests aim to be **realistic** and not overly extensive.
   - **No integration tests** were included, following the stated requirements.

6. **Example Response Discrepancy**  
   In example response, for year 2023 the **DAMAGE** premium for an **OTHER** make with a sum insured of 200 and bike manufacture year
   2019 is shown as **11.00**. However, when manually calculated it should be **7.67**.

   **Calculation**:
   1. **Sum Insured** is halved for DAMAGE: `200 / 2 = 100`.
   2. **Base Premium** for DAMAGE is **10**.
   3. **Sum Insured Factor** (for 100) is **0.5**.
   4. **Age Factor** (for age 4 with “OTHER” make) interpolates to ~**1.53**.

   **Final Premium** = `10 * 0.5 * 1.53 ≈ 7.67`.

7. **Missing Coverage Type Documentation**  
   Coverage type is present in examples and swagger documentation, but has no explicit functionality in requirements. For now, it is simply copied to response.

8. **Exception handling**  
   All runtime exceptions are handled by a global exception handler that produces a consistent `ApiError` response.
   Missing risk scripts trigger a dedicated `MissingScriptException`, which is mapped to HTTP 400 because it occurs when an unsupported risk type is used in a request.

## Running & Usage

1. **Build**:
   ```bash
   ./gradlew clean build
   ```

2. **Run**:
   ```bash
   ./gradlew bootRun
   ```

3. **Endpoint**:
   ```
   POST /api/v1/calculate
   ```