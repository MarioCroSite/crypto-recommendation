package com.mario.cryptorecommendation.infrastructure.ingestion.symbolpricesummary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SymbolPriceSummaryJpaRepository extends JpaRepository<SymbolPriceSummaryEntity, SymbolPriceSummaryId> {

    @Query(value = """
           SELECT sps1.* FROM symbol_price_summary sps1
           INNER JOIN (
                SELECT symbol, MAX(period_end) AS latest_period_end
                FROM symbol_price_summary
                GROUP BY symbol
           ) sps2 ON sps1.symbol = sps2.symbol AND sps1.period_end = sps2.latest_period_end
         """, nativeQuery = true)
    List<SymbolPriceSummaryEntity> findLatestSummaryPrices();

    @Query(value = """
           SELECT * FROM symbol_price_summary
           WHERE symbol = :symbol
           ORDER BY period_end DESC LIMIT 1
         """, nativeQuery = true)
    Optional<SymbolPriceSummaryEntity> findLatestSummaryPricesBySymbol(String symbol);
}
