package com.mario.cryptorecommendation.domain.ingestion.reader;

import java.math.BigDecimal;
import java.time.Instant;

public record Statement(
        Instant timestamp,
        String symbol,
        BigDecimal price
) {

}
