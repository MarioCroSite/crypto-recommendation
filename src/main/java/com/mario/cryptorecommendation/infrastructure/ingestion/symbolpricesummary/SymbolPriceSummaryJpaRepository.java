package com.mario.cryptorecommendation.infrastructure.ingestion.symbolpricesummary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SymbolPriceSummaryJpaRepository extends JpaRepository<SymbolPriceSummaryEntity, SymbolPriceSummaryId> {

}
