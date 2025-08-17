package com.mario.cryptorecommendation.domain.ingestion.symbolpricesummary;

import com.mario.cryptorecommendation.domain.ingestion.Period;
import com.mario.cryptorecommendation.domain.ingestion.aggregator.AggregatedStatus;

import java.math.BigDecimal;

import static com.mario.cryptorecommendation.domain.ingestion.aggregator.AggregatedStatus.CONFLICT;
import static java.math.BigDecimal.ZERO;

public record SymbolPriceSummary(
        String symbol,
        Period period,
        AggregatedStatus status,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        BigDecimal oldestPrice,
        BigDecimal newestPrice,
        BigDecimal normalizedRange
) {

    public static SymbolPriceSummary conflict(String symbol, Period period) {
        return new SymbolPriceSummary(symbol, period, CONFLICT, ZERO, ZERO, ZERO, ZERO, ZERO);
    }

    public static SymbolPriceSummary of(String symbol, Period period, AggregatedStatus status,
                                        BigDecimal minPrice, BigDecimal maxPrice,
                                        BigDecimal oldestPrice, BigDecimal newestPrice,
                                        BigDecimal normalizedRange) {

        return new SymbolPriceSummary(symbol, period, status, minPrice, maxPrice, oldestPrice, newestPrice, normalizedRange);
    }
}
