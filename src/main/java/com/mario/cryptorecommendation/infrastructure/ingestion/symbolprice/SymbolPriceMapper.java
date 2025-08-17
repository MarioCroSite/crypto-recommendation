package com.mario.cryptorecommendation.infrastructure.ingestion.symbolprice;

import com.mario.cryptorecommendation.domain.ingestion.symbolprice.SymbolPrice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface SymbolPriceMapper {

    @Mapping(target = "symbol", source = "id.symbol")
    @Mapping(target = "createdAt", source = "id.createdAt")
    SymbolPrice toDomain(SymbolPriceEntity symbolPriceEntity);

    @Mapping(target = "id.symbol", source = "symbol")
    @Mapping(target = "id.createdAt", source = "createdAt")
    SymbolPriceEntity toEntity(SymbolPrice domain);
}
