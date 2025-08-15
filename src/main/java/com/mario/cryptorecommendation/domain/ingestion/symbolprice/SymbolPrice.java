package com.mario.cryptorecommendation.domain.ingestion.symbolprice;


import java.math.BigDecimal;
import java.time.Instant;

public record SymbolPrice(
        String symbol,
        Instant createdAt,
        BigDecimal price
) {

}
