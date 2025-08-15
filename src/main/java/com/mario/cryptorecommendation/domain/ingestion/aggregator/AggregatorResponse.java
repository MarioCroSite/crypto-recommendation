package com.mario.cryptorecommendation.domain.ingestion.aggregator;

import com.mario.cryptorecommendation.domain.ingestion.Period;
import com.mario.cryptorecommendation.domain.ingestion.symbolprice.SymbolPrice;

import java.util.List;

public record AggregatorResponse(
        List<SymbolPrice> aggregatedSymbolPrices,
        AggregatedStatus aggregatedStatus,
        Period period,
        String symbol
) {

}
