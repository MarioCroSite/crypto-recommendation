package com.mario.cryptorecommendation.domain.ingestion.symbolpricesummary;

import com.mario.cryptorecommendation.domain.ingestion.aggregator.AggregatorResponse;
import com.mario.cryptorecommendation.domain.ingestion.symbolprice.SymbolPrice;
import com.mario.cryptorecommendation.domain.utils.NormalizedRangeCalculator;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

import static com.mario.cryptorecommendation.domain.ingestion.aggregator.AggregatedStatus.CONFLICT;
import static java.math.BigDecimal.ZERO;
import static java.util.Comparator.comparing;

@Component
public class SymbolPriceEvaluator {

    public SymbolPriceSummary evaluate(AggregatorResponse result) {
        var period = result.period();
        var status = result.status();
        var symbol = result.symbol();

        if (status == CONFLICT) {
            return SymbolPriceSummary.conflict(symbol, period);
        }

        var aggregatedSymbolPrices = result.aggregatedSymbolPrices();

        var minPrice = getMinPrice(aggregatedSymbolPrices);
        var maxPrice = getMaxPrice(aggregatedSymbolPrices);
        var oldestPrice = getOldestPrice(aggregatedSymbolPrices);
        var newestPrice = getNewestPrice(aggregatedSymbolPrices);
        var normalizedRange = NormalizedRangeCalculator.calculate(minPrice, maxPrice);

        return SymbolPriceSummary.of(result.symbol(), period, status, minPrice, maxPrice,
                oldestPrice, newestPrice, normalizedRange);
    }

    private BigDecimal getMinPrice(List<SymbolPrice> symbolPrices) {
        return symbolPrices.stream()
                .map(SymbolPrice::price)
                .min(BigDecimal::compareTo)
                .orElse(ZERO);
    }

    private BigDecimal getMaxPrice(List<SymbolPrice> symbolPrices) {
        return symbolPrices.stream()
                .map(SymbolPrice::price)
                .max(BigDecimal::compareTo)
                .orElse(ZERO);
    }

    private BigDecimal getOldestPrice(List<SymbolPrice> symbolPrices) {
        return symbolPrices.stream()
                .min(comparing(SymbolPrice::createdAt))
                .map(SymbolPrice::price)
                .orElse(ZERO);
    }

    private BigDecimal getNewestPrice(List<SymbolPrice> symbolPrices) {
        return symbolPrices.stream()
                .max(comparing(SymbolPrice::createdAt))
                .map(SymbolPrice::price)
                .orElse(ZERO);
    }
}
