package com.mario.cryptorecommendation.domain.recommendation;

public class NoDataFoundException extends RuntimeException {
    public NoDataFoundException(String message) {
        super(message);
    }
}
