package com.mario.cryptorecommendation.infrastructure.ingestion.symbolpricesummary;

import com.mario.cryptorecommendation.domain.ingestion.symbolpricesummary.SymbolPriceSummary;
import com.mario.cryptorecommendation.domain.ingestion.symbolpricesummary.SymbolPriceSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SymbolPriceSummaryAdapter implements SymbolPriceSummaryRepository {

    private final SymbolPriceSummaryJpaRepository symbolPriceSummaryJpaRepository;
    private final SymbolPriceSummaryMapper mapper;

    @Override
    public SymbolPriceSummary save(SymbolPriceSummary symbolPriceSummary) {
        var summary = symbolPriceSummaryJpaRepository.save(mapper.toEntity(symbolPriceSummary));
        return mapper.toDomain(summary);
    }
}
