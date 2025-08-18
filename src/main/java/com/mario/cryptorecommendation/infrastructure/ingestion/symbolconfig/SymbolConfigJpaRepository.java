package com.mario.cryptorecommendation.infrastructure.ingestion.symbolconfig;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SymbolConfigJpaRepository extends JpaRepository<SymbolConfigEntity, String> {

}
