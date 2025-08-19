package com.mario.cryptorecommendation.domain.ingestion.symbolconfig;

import java.time.Instant;
import java.util.function.Function;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.YEARS;

public enum TimeFrame {
    MONTHLY(endDate -> endDate.atOffset(UTC).toLocalDateTime().minus(1, MONTHS).plus(1, DAYS).toInstant(UTC)),
    HALF_YEARLY(endDate -> endDate.atOffset(UTC).toLocalDateTime().minus(6, MONTHS).plus(1, DAYS).toInstant(UTC)),
    YEARLY(endDate -> endDate.atOffset(UTC).toLocalDateTime().minus(1, YEARS).plus(1, DAYS).toInstant(UTC));

    private final Function<Instant, Instant> startFrame;

    TimeFrame(Function<Instant, Instant> startFrame) {
        this.startFrame = startFrame;
    }

    public Instant findStartFrame(Instant endFrame) {
        return startFrame.apply(endFrame);
    }
}
