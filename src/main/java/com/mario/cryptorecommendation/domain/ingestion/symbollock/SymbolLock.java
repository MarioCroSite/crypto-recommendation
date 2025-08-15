package com.mario.cryptorecommendation.domain.ingestion.symbollock;

import java.time.Instant;

public record SymbolLock(
        String currency,
        boolean locked,
        Instant lockedAt
) {

}
