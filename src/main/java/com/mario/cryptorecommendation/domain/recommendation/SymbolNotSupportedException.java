package com.mario.cryptorecommendation.domain.recommendation;

public class SymbolNotSupportedException extends RuntimeException {
    public SymbolNotSupportedException(String message) {
        super(message);
    }
}
