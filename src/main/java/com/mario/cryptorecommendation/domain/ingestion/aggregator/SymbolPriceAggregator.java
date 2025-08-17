package com.mario.cryptorecommendation.domain.ingestion.aggregator;

import com.mario.cryptorecommendation.domain.ingestion.Period;
import com.mario.cryptorecommendation.domain.ingestion.symbolprice.SymbolPrice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.mario.cryptorecommendation.domain.ingestion.aggregator.AggregatedStatus.INCOMPLETE;
import static com.mario.cryptorecommendation.domain.ingestion.aggregator.AggregatedStatus.COMPLETE;
import static com.mario.cryptorecommendation.domain.ingestion.aggregator.AggregatedStatus.CONFLICT;
import static java.time.ZoneOffset.UTC;
import static java.util.stream.Collectors.toSet;

@Slf4j
@Component
public class SymbolPriceAggregator {

    public AggregatorResponse aggregate(AggregatorRequest request) {
        var symbol = request.getSymbol();
        var period = request.period();

        if (hasConflicts(request.existingSymbolPrices(), request.newSymbolPrices())) {
            return new AggregatorResponse(null, CONFLICT, period, symbol);
        }

        var aggregatedSymbolPrices = mergeSymbolPrices(request.existingSymbolPrices(), request.newSymbolPrices());
        var status = getAggregateStatus(aggregatedSymbolPrices, period, symbol);

        return new AggregatorResponse(aggregatedSymbolPrices, status, period, symbol);
    }


    private boolean hasConflicts(List<SymbolPrice> existingSymbolPrices, List<SymbolPrice> newSymbolPrices) {
        var existingSymbolPricesSet = new HashSet<>(existingSymbolPrices);

        var conflict = newSymbolPrices.stream()
                .filter(existingSymbolPricesSet::contains)
                .findFirst();

        conflict.ifPresent(symbolPrice ->
                log.warn("Data conflicts detected for symbol {}. SymbolPrice already exists for timestamp {}",
                        symbolPrice.symbol(), symbolPrice.createdAt())
        );

        return conflict.isPresent();
    }

    private List<SymbolPrice> mergeSymbolPrices(List<SymbolPrice> existingSymbolPrices, List<SymbolPrice> newSymbolPrices) {
        var mergedSymbolPrices = new HashSet<>(existingSymbolPrices);
        mergedSymbolPrices.addAll(newSymbolPrices);

        return mergedSymbolPrices.stream()
                .sorted((sp1, sp2) -> sp2.createdAt().compareTo(sp1.createdAt())) // Sort descending
                .toList();
    }

    private AggregatedStatus getAggregateStatus(
            List<SymbolPrice> aggregatedSymbolPrices, Period period, String symbol) {

        var requiredDates = period.getAllDates();
        var availableDates = getAvailableDates(aggregatedSymbolPrices);

        if (availableDates.containsAll(requiredDates)) {
            return COMPLETE;
        } else {
            var missingDates = new HashSet<>(requiredDates);
            missingDates.removeAll(availableDates);
            log.warn("Data incomplete for symbol {} in period {}. Missing dates: {}", symbol, period, missingDates);
            return INCOMPLETE;
        }
    }

    private Set<Instant> getAvailableDates(List<SymbolPrice> aggregatedSymbolPrices) {
        return aggregatedSymbolPrices.stream()
                .map(rate -> rate.createdAt().atZone(UTC)
                        .toLocalDate()
                        .atStartOfDay(UTC)
                        .toInstant())
                .collect(toSet());
    }
}
