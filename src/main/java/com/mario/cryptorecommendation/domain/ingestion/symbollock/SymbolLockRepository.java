package com.mario.cryptorecommendation.domain.ingestion.symbollock;

import java.util.Optional;

public interface SymbolLockRepository {
    Optional<SymbolLock> findBySymbol(String symbol);
    boolean lockSymbol(String symbol);
    boolean unlockSymbol(String symbol);
}
