package com.mario.cryptorecommendation.infrastructure.ingestion.symbolpricesummary;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SymbolPriceSummaryId {

    @Column(name = "symbol", nullable = false, length = 20)
    private String symbol;

    @Column(name = "period_start", nullable = false)
    private Instant periodStart;
}
