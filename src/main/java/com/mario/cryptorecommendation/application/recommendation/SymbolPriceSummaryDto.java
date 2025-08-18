package com.mario.cryptorecommendation.application.recommendation;

import java.math.BigDecimal;

public record SymbolPriceSummaryDto(
        String symbol,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        BigDecimal oldestPrice,
        BigDecimal newestPrice,
        BigDecimal normalizedRange
) {

}
