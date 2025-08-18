package com.mario.cryptorecommendation.application.recommendation;

import com.mario.cryptorecommendation.domain.ingestion.symbolpricesummary.SymbolPriceSummary;
import org.mapstruct.Mapper;

@Mapper
public interface RecommendationDtoMapper {

    SymbolPriceSummaryDto toDto(SymbolPriceSummary symbolPriceSummary);
}
