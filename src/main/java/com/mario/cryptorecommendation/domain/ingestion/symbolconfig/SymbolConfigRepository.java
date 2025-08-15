package com.mario.cryptorecommendation.domain.ingestion.symbolconfig;

import java.util.Optional;

public interface SymbolConfigRepository {
    Optional<SymbolConfig> findBySymbol(String symbol);
}
