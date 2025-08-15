package com.mario.cryptorecommendation.infrastructure.ingestion.symbollock;

import com.mario.cryptorecommendation.domain.ingestion.symbollock.SymbolLock;
import org.mapstruct.Mapper;

@Mapper
public interface SymbolLockMapper {

    SymbolLock toDomain(SymbolLockEntity entity);
}
