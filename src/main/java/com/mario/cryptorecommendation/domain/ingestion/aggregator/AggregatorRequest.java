package com.mario.cryptorecommendation.domain.ingestion.aggregator;

import com.mario.cryptorecommendation.domain.ingestion.Period;
import com.mario.cryptorecommendation.domain.ingestion.symbolprice.SymbolPrice;
import lombok.Builder;

import java.util.List;

@Builder
public record AggregatorRequest(
        List<SymbolPrice> existingSymbolPrices,
        List<SymbolPrice> newSymbolPrices,
        Period period
) {

    public AggregatorRequest {
        if (newSymbolPrices.isEmpty()) {
            throw new IllegalArgumentException("New symbol prices cannot be empty");
        }

        var symbol = newSymbolPrices.getFirst().symbol();
        var allSameSymbol = newSymbolPrices.stream().allMatch(symbolPrice -> symbolPrice.symbol().equals(symbol));

        if (!allSameSymbol) {
            throw new IllegalArgumentException("All symbol prices must be for the same symbol");
        }
    }

    public String getSymbol() {
        return newSymbolPrices.getFirst().symbol();
    }
}
