package com.mario.cryptorecommendation.domain.ingestion;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

import static java.time.ZoneOffset.UTC;
import static java.util.stream.Collectors.toSet;

public record Period(
        Instant start,
        Instant end
) {

    public Period {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start cannot be after end");
        }
    }

    public static Period of(Instant start, Instant end) {
        return new Period(start, end);
    }

    public Set<Instant> getAllDates() {
        LocalDate startDate = start.atZone(UTC).toLocalDate();
        LocalDate endDate = end.atZone(UTC).toLocalDate();

        return startDate.datesUntil(endDate.plusDays(1))
                .map(date -> date.atStartOfDay(UTC).toInstant())
                .collect(toSet());
    }

}
