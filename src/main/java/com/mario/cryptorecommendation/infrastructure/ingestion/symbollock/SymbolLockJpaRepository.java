package com.mario.cryptorecommendation.infrastructure.ingestion.symbollock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Repository
public interface SymbolLockJpaRepository extends JpaRepository<SymbolLockEntity, String> {

    /**
     * Locks a symbol by its symbol code.
     *
     * @param symbol the symbol to lock
     * @return the number of records updated (0 if symbol was already locked, 1 if lock was successful)
     */
    @Modifying
    @Query("UPDATE SymbolLockEntity sl SET sl.locked = true, sl.lockedAt = :timestamp WHERE sl.symbol = :symbol AND sl.locked = false")
    @Transactional
    int lockCurrency(String symbol, Instant timestamp);

    /**
     * Unlocks a symbol by its symbol code.
     *
     * @param symbol the symbol to unlock
     * @return the number of records updated (0 if symbol was already unlocked, 1 if unlock was successful)
     */
    @Modifying
    @Query("UPDATE SymbolLockEntity sl SET sl.locked = false, sl.lockedAt = NULL WHERE sl.symbol = :symbol AND sl.locked = true")
    @Transactional
    int unlockCurrency(String symbol);
}
