package com.mario.cryptorecommendation.infrastructure.ingestion.symbolpricesummary;

import com.mario.cryptorecommendation.domain.ingestion.symbolpricesummary.SymbolPriceSummary;
import com.mario.cryptorecommendation.domain.ingestion.symbolpricesummary.SymbolPriceSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static java.util.Comparator.comparing;

@Repository
@RequiredArgsConstructor
public class SymbolPriceSummaryAdapter implements SymbolPriceSummaryRepository {

    private final SymbolPriceSummaryJpaRepository symbolPriceSummaryJpaRepository;
    private final SymbolPriceSummaryMapper mapper;

    @CacheEvict(value = {
            "latestSummaryPricesByNormalizedRangeDesc",
            "symbolWithHighestNormalizedRangeInDay",
            "latestSymbolPriceForSymbol"},
            allEntries = true)
    @Override
    public SymbolPriceSummary save(SymbolPriceSummary symbolPriceSummary) {
        var summary = symbolPriceSummaryJpaRepository.save(mapper.toEntity(symbolPriceSummary));
        return mapper.toDomain(summary);
    }

    @Override
    public List<SymbolPriceSummary> findLatestSummaryPricesOrderedByNormalizedRangeDesc() {
        return symbolPriceSummaryJpaRepository.findLatestSummaryPrices().stream()
                .map(mapper::toDomain)
                .sorted(comparing(SymbolPriceSummary::normalizedRange).reversed())
                .toList();
    }

    @Override
    public Optional<SymbolPriceSummary> findLatestSummaryPricesBySymbol(String symbol) {
        return symbolPriceSummaryJpaRepository.findLatestSummaryPricesBySymbol(symbol).map(mapper::toDomain);
    }
}
