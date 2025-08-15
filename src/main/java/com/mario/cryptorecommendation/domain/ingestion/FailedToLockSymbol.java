package com.mario.cryptorecommendation.domain.ingestion;

public class FailedToLockSymbol extends RuntimeException {

    public FailedToLockSymbol(String message) {
        super(message);
    }
}
