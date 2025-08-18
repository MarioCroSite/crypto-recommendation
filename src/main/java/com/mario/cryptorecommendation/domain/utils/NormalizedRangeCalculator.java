package com.mario.cryptorecommendation.domain.utils;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;

public class NormalizedRangeCalculator {

    private static final int PRECISION = 6;

    public static BigDecimal calculate(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice.compareTo(ZERO) <= 0) {
            return ZERO;
        }
        return maxPrice.subtract(minPrice)
                .divide(minPrice, PRECISION, HALF_UP);
    }
}
