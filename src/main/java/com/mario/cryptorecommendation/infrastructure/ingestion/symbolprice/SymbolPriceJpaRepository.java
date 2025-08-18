package com.mario.cryptorecommendation.infrastructure.ingestion.symbolprice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface SymbolPriceJpaRepository extends JpaRepository<SymbolPriceEntity, SymbolPriceId> {

    @Query("SELECT sp FROM SymbolPriceEntity sp WHERE sp.id.symbol = :symbol AND sp.id.createdAt BETWEEN :start AND :end")
    List<SymbolPriceEntity> findBySymbolAndDateTimeRange(String symbol, Instant start, Instant end);
}
