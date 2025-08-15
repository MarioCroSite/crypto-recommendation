package com.mario.cryptorecommendation.domain.ingestion.reader;

public class FileReadFailedException extends RuntimeException {
    public FileReadFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
