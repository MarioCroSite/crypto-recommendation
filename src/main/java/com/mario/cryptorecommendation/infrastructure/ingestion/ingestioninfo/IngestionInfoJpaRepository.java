package com.mario.cryptorecommendation.infrastructure.ingestion.ingestioninfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngestionInfoJpaRepository extends JpaRepository<IngestionInfoEntity, Long> {

}
