package com.mario.cryptorecommendation.domain.ingestion.symbolprice;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SymbolPriceRepository {
    List<SymbolPrice> findBySymbolAndDateTimeRange(String symbolPrice, Instant start, Instant end);
    List<SymbolPrice> saveAll(List<SymbolPrice> symbolPrices);
    Optional<String> findSymbolWithHighestNormalizedRangeInDay(LocalDate date);
}
