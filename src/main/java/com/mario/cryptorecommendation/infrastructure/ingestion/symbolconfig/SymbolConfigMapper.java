package com.mario.cryptorecommendation.infrastructure.ingestion.symbolconfig;

import com.mario.cryptorecommendation.domain.ingestion.symbolconfig.SymbolConfig;
import org.mapstruct.Mapper;

@Mapper
public interface SymbolConfigMapper {

    SymbolConfig toDomain(SymbolConfigEntity entity);
}
