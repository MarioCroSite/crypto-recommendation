package com.mario.cryptorecommendation.infrastructure.ingestion.symbolprice;

import com.mario.cryptorecommendation.domain.ingestion.symbolprice.SymbolPrice;
import org.mapstruct.Mapper;

@Mapper
public interface SymbolPriceMapper {

    SymbolPrice toDomain(SymbolPriceEntity symbolPriceEntity);
}
