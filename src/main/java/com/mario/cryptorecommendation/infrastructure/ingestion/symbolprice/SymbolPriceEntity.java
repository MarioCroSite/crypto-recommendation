package com.mario.cryptorecommendation.infrastructure.ingestion.symbolprice;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "symbol_price")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
public class SymbolPriceEntity {

    @EmbeddedId
    private SymbolPriceId id;

    @Column(name = "price", nullable = false, precision = 18, scale = 6)
    private BigDecimal price;
}
