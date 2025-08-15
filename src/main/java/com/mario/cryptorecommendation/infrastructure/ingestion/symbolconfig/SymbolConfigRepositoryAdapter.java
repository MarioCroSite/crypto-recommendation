package com.mario.cryptorecommendation.infrastructure.ingestion.symbolconfig;

import com.mario.cryptorecommendation.domain.ingestion.symbolconfig.SymbolConfig;
import com.mario.cryptorecommendation.domain.ingestion.symbolconfig.SymbolConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SymbolConfigRepositoryAdapter implements SymbolConfigRepository {

    private final SymbolConfigJpaRepository symbolConfigJpaRepository;
    private final SymbolConfigMapper symbolConfigMapper;

    @Override
    public Optional<SymbolConfig> findBySymbol(String symbol) {
        return symbolConfigJpaRepository.findById(symbol).map(symbolConfigMapper::toDomain);
    }
}
