package com.mario.cryptorecommendation.infrastructure.ingestion.symbolprice;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SymbolPriceId implements Serializable {

    private String symbol;

    @Column(name = "created_at")
    private Instant createdAt;
}
