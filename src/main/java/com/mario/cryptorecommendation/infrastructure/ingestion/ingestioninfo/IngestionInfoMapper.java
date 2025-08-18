package com.mario.cryptorecommendation.infrastructure.ingestion.ingestioninfo;

import com.mario.cryptorecommendation.domain.ingestion.ingestioninfo.IngestionInfo;
import org.mapstruct.Mapper;

@Mapper
public interface IngestionInfoMapper {

    IngestionInfo toDomain(IngestionInfoEntity entity);

    IngestionInfoEntity toEntity(IngestionInfo domain);

}
