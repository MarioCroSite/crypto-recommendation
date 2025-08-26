package com.mario.cryptorecommendation.domain.ingestion.symbolpricesummary;

import java.util.List;
import java.util.Optional;

public interface SymbolPriceSummaryRepository {

    SymbolPriceSummary save(SymbolPriceSummary symbolPriceSummary);
    List<SymbolPriceSummary> findLatestSummaryPricesOrderedByNormalizedRangeDesc();
    Optional<SymbolPriceSummary> findLatestSummaryPricesBySymbol(String symbol);
}
