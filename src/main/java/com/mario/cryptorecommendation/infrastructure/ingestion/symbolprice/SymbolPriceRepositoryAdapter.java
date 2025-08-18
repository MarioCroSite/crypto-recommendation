package com.mario.cryptorecommendation.infrastructure.ingestion.symbolprice;

import com.mario.cryptorecommendation.domain.ingestion.symbolprice.SymbolPrice;
import com.mario.cryptorecommendation.domain.ingestion.symbolprice.SymbolPriceRepository;
import com.mario.cryptorecommendation.domain.utils.NormalizedRangeCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.math.BigDecimal.ZERO;
import static java.time.LocalTime.MAX;
import static java.time.ZoneOffset.UTC;

@Component
@RequiredArgsConstructor
public class SymbolPriceRepositoryAdapter implements SymbolPriceRepository {

    private final SymbolPriceJpaRepository symbolPriceJpaRepository;
    private final SymbolPriceMapper symbolPriceMapper;

    @Override
    public List<SymbolPrice> findBySymbolAndDateTimeRange(String symbolPrice, Instant start, Instant end) {
        return symbolPriceJpaRepository.findBySymbolAndDateTimeRange(symbolPrice, start, end).stream()
                .map(symbolPriceMapper::toDomain)
                .toList();
    }

    @Override
    public List<SymbolPrice> saveAll(List<SymbolPrice> symbolPrices) {
        var symbolPriceEntities = symbolPrices.stream()
                .map(symbolPriceMapper::toEntity)
                .toList();
        var savedEntities = symbolPriceJpaRepository.saveAll(symbolPriceEntities);

        return StreamSupport.stream(savedEntities.spliterator(), false)
                .map(symbolPriceMapper::toDomain)
                .toList();
    }

    @Override
    public String findSymbolWithHighestNormalizedRangeInDay(LocalDate date) {
        var startDate = date.atStartOfDay(UTC).toInstant();
        var endDate = date.atTime(MAX).atZone(UTC).toInstant();

        var symbolPricesByDate = symbolPriceJpaRepository.findByDateTimeBetween(startDate, endDate);
        if(symbolPricesByDate.isEmpty()) {
            throw new IllegalArgumentException("No symbol prices found for the given day: %s".formatted(date));
        }

        // Group rates by symbol and calculate normalization rate for each symbol
        return symbolPricesByDate.stream()
                .collect(Collectors.groupingBy(symbolPrice -> symbolPrice.getId().getSymbol()))
                .entrySet()
                .stream()
                .map(this::mapToSymbolAndNormalizedRange)
                .max(Comparator.comparing(Pair::getSecond))
                .map(Pair::getFirst)
                .orElse("No symbol found");
    }

    private Pair<String, BigDecimal> mapToSymbolAndNormalizedRange(Map.Entry<String, List<SymbolPriceEntity>> stringListEntry) {
        var symbol = stringListEntry.getKey();
        var symbolPrices = stringListEntry.getValue();

        var minPrice = symbolPrices.stream().map(SymbolPriceEntity::getPrice).min(BigDecimal::compareTo).orElse(ZERO);
        var maxPrice = symbolPrices.stream().map(SymbolPriceEntity::getPrice).max(BigDecimal::compareTo).orElse(ZERO);
        var normalizedRange = NormalizedRangeCalculator.calculate(minPrice, maxPrice);

        return Pair.of(symbol, normalizedRange);
    }
}
