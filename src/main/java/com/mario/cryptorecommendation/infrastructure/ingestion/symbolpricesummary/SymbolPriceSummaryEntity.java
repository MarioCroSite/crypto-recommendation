package com.mario.cryptorecommendation.infrastructure.ingestion.symbolpricesummary;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "symbol_price_summary")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
public class SymbolPriceSummaryEntity {

    @EmbeddedId
    private SymbolPriceSummaryId id;

    @Column(name = "period_end", nullable = false)
    private Instant periodEnd;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AggregatedStatus status;

    @Column(name = "min_price", nullable = false, precision = 18, scale = 6)
    private BigDecimal minPrice;

    @Column(name = "max_price", nullable = false, precision = 18, scale = 6)
    private BigDecimal maxPrice;

    @Column(name = "oldest_price", nullable = false, precision = 18, scale = 6)
    private BigDecimal oldestPrice;

    @Column(name = "newest_price", nullable = false, precision = 18, scale = 6)
    private BigDecimal newestPrice;

    @Column(name = "normalized_range", nullable = false, precision = 18, scale = 6)
    private BigDecimal normalizedRange;
}
