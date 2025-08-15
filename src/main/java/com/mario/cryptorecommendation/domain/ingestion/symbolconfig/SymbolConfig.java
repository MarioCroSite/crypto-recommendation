package com.mario.cryptorecommendation.domain.ingestion.symbolconfig;

import java.time.Instant;

public record SymbolConfig(
        String symbol,
        TimeFrame timeFrame,
        Instant createdAt,
        Instant updatedAt
) {

}
