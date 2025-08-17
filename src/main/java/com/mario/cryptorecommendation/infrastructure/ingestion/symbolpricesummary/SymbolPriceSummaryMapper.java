package com.mario.cryptorecommendation.infrastructure.ingestion.symbolpricesummary;

import com.mario.cryptorecommendation.domain.ingestion.Period;
import com.mario.cryptorecommendation.domain.ingestion.symbolpricesummary.SymbolPriceSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;

@Mapper
public interface SymbolPriceSummaryMapper {

    @Mapping(target = "symbol", source = "id.symbol")
    @Mapping(target = "period", expression = "java(createPeriod(entity.getId().getPeriodStart(), entity.getPeriodEnd()))")
    SymbolPriceSummary toDomain(SymbolPriceSummaryEntity symbolPriceSummaryEntity);

    @Mapping(target = "id.symbol", source = "symbol")
    @Mapping(target = "id.periodStart", source = "period.start")
    @Mapping(target = "periodEnd", source = "period.end")
    SymbolPriceSummaryEntity toEntity(SymbolPriceSummary domain);

    default Period createPeriod(Instant start, Instant end) {
        return new Period(start, end);
    }
}
