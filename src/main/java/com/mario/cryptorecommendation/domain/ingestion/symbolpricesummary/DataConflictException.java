package com.mario.cryptorecommendation.domain.ingestion.symbolpricesummary;

public class DataConflictException extends RuntimeException {
    public DataConflictException(String message) {
        super(message);
    }
}
