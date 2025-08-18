package com.mario.cryptorecommendation.domain.ingestion.symbollock;

public class FailedToLockSymbol extends RuntimeException {

    public FailedToLockSymbol(String message) {
        super(message);
    }
}
