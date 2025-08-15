package com.mario.cryptorecommendation.infrastructure.ingestion.symbollock;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "symbol_lock")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
public class SymbolLockEntity {

    @Id
    private String symbol;

    @Column(name = "locked")
    private boolean locked;

    @Column(name = "locked_at")
    private Instant lockedAt;
}
