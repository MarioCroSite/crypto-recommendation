package com.mario.cryptorecommendation.infrastructure.ingestion.symbollock;

import com.mario.cryptorecommendation.domain.ingestion.symbollock.SymbolLock;
import com.mario.cryptorecommendation.domain.ingestion.symbollock.SymbolLockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SymbolLockRepositoryAdapter implements SymbolLockRepository {

    private final SymbolLockJpaRepository symbolLockJpaRepository;
    private final SymbolLockMapper symbolLockMapper;

    @Override
    public Optional<SymbolLock> findBySymbol(String symbol) {
        return symbolLockJpaRepository.findById(symbol).map(symbolLockMapper::toDomain);
    }

    @Override
    public boolean lockSymbol(String symbol) {
        var locked = symbolLockJpaRepository.lockCurrency(symbol, Instant.now());
        return locked > 0;
    }

    @Override
    public boolean unlockSymbol(String symbol) {
        var unlocked = symbolLockJpaRepository.unlockCurrency(symbol);
        return unlocked > 0;
    }
}
