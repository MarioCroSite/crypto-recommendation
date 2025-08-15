package com.mario.cryptorecommendation.domain;

public record CryptoRate(
        String symbol,
        double minRate,
        double maxRate,
        double oldestRate,
        double latestRate
) {

}
