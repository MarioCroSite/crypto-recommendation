package com.mario.cryptorecommendation.domain.ingestion.symbolprice;

import java.time.Instant;
import java.util.List;

public interface SymbolPriceRepository {
    List<SymbolPrice> findBySymbolAndDateTimeRange(String symbolPrice, Instant start, Instant end);
    List<SymbolPrice> saveAll(List<SymbolPrice> symbolPrices);
}
