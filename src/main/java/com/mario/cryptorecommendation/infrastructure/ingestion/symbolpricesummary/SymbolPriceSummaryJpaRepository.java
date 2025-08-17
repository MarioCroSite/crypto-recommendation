package com.mario.cryptorecommendation.infrastructure.ingestion.symbolpricesummary;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SymbolPriceSummaryJpaRepository extends CrudRepository<SymbolPriceSummaryEntity, SymbolPriceSummaryId> {

}
