package com.mario.cryptorecommendation.infrastructure.ingestion.symbolconfig;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SymbolConfigJpaRepository extends CrudRepository<SymbolConfigEntity, String> {

}
