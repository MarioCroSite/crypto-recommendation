package com.mario.cryptorecommendation.infrastructure.ingestion.symbolpricesummary;

import com.mario.cryptorecommendation.domain.ingestion.Period;
import com.mario.cryptorecommendation.domain.ingestion.aggregator.AggregatedStatus;
import com.mario.cryptorecommendation.domain.ingestion.symbolpricesummary.SymbolPriceSummary;
import org.mapstruct.Mapper;

@Mapper
public interface SymbolPriceSummaryMapper {

    default SymbolPriceSummary toDomain(SymbolPriceSummaryEntity entity) {
        var period = new Period(entity.getId().getPeriodStart(), entity.getPeriodEnd());
        return new SymbolPriceSummary(
                entity.getId().getSymbol(),
                period,
                AggregatedStatus.valueOf(entity.getStatus().name()),
                entity.getMinPrice(),
                entity.getMaxPrice(),
                entity.getOldestPrice(),
                entity.getNewestPrice(),
                entity.getNormalizedRange()
        );
    }

    default SymbolPriceSummaryEntity toEntity(SymbolPriceSummary domain) {
        var id = new SymbolPriceSummaryId(domain.symbol(), domain.period().start());
        return SymbolPriceSummaryEntity.builder()
                .id(id)
                .periodEnd(domain.period().end())
                .status(com.mario.cryptorecommendation.infrastructure.ingestion.symbolpricesummary.AggregatedStatus.valueOf(domain.status().name()))
                .minPrice(domain.minPrice())
                .maxPrice(domain.maxPrice())
                .oldestPrice(domain.oldestPrice())
                .newestPrice(domain.newestPrice())
                .normalizedRange(domain.normalizedRange())
                .build();
    }
}
