package com.mario.cryptorecommendation.domain.ingestion.symbolconfig;

import java.time.Instant;
import java.util.function.Function;

import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.YEARS;

public enum TimeFrame {
    MONTHLY(endDate -> endDate.minus(1, MONTHS).plus(1, DAYS)),
    HALF_YEARLY(endDate -> endDate.minus(6, MONTHS).plus(1, DAYS)),
    YEARLY(endDate -> endDate.minus(1, YEARS).plus(1, DAYS));

    private final Function<Instant, Instant> startFrame;

    TimeFrame(Function<Instant, Instant> startFrame) {
        this.startFrame = startFrame;
    }

    public Instant findStartFrame(Instant endFrame) {
        return startFrame.apply(endFrame);
    }
}
