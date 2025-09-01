package com.mario.cryptorecommendation.domain.ingestion.symbolpricesummary;

import com.mario.cryptorecommendation.domain.ingestion.Period;
import com.mario.cryptorecommendation.domain.ingestion.aggregator.AggregatorResponse;
import com.mario.cryptorecommendation.domain.ingestion.symbolprice.SymbolPrice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static com.mario.cryptorecommendation.domain.ingestion.aggregator.AggregatedStatus.*;
import static java.math.BigDecimal.ZERO;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SymbolPriceEvaluatorTest {

    private SymbolPriceEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new SymbolPriceEvaluator();
    }

    @Test
    void shouldCreateConflictSummary() {
        // Given
        var symbol = "BTC";
        var period = new Period(
                LocalDate.of(2024, 1, 1).atStartOfDay(UTC).toInstant(),
                LocalDate.of(2024, 1, 3).atStartOfDay(UTC).toInstant()
        );

        var response = new AggregatorResponse(
                null, // aggregatedSymbolPrices is null for conflicts
                CONFLICT,
                period,
                symbol
        );

        // When
        var summary = evaluator.evaluate(response);

        // Then
        assertThat(summary.symbol()).isEqualTo(symbol);
        assertThat(summary.period()).isEqualTo(period);
        assertThat(summary.status()).isEqualTo(CONFLICT);
        assertThat(summary.minPrice()).isEqualTo(ZERO);
        assertThat(summary.maxPrice()).isEqualTo(ZERO);
        assertThat(summary.oldestPrice()).isEqualTo(ZERO);
        assertThat(summary.newestPrice()).isEqualTo(ZERO);
        assertThat(summary.normalizedRange()).isEqualTo(ZERO);
    }

    @Test
    void shouldCreateCompleteSummary() {
        // Given
        var period = new Period(
                LocalDate.of(2024, 1, 1).atStartOfDay(UTC).toInstant(),
                LocalDate.of(2024, 1, 3).atStartOfDay(UTC).toInstant()
        );

        var symbolPrices = List.of(
                createSymbolPrice("BTC", "2024-01-01T10:00:00Z", "50000.00"),
                createSymbolPrice("BTC", "2024-01-02T10:00:00Z", "51000.00"),
                createSymbolPrice("BTC", "2024-01-03T10:00:00Z", "52000.00")
        );

        var response = new AggregatorResponse(
                symbolPrices,
                COMPLETE,
                period,
                "BTC"
        );

        // When
        var summary = evaluator.evaluate(response);

        // Then
        assertThat(summary.symbol()).isEqualTo("BTC");
        assertThat(summary.period()).isEqualTo(period);
        assertThat(summary.status()).isEqualTo(COMPLETE);
        assertThat(summary.minPrice()).isEqualTo(new BigDecimal("50000.00"));
        assertThat(summary.maxPrice()).isEqualTo(new BigDecimal("52000.00"));
        assertThat(summary.oldestPrice()).isEqualTo(new BigDecimal("50000.00"));
        assertThat(summary.newestPrice()).isEqualTo(new BigDecimal("52000.00"));
        assertThat(summary.normalizedRange()).isEqualTo(new BigDecimal("0.040000")); // (52000-50000)/50000
    }

    @Test
    void shouldCreateIncompleteSummary() {
        // Given
        var period = new Period(
                LocalDate.of(2024, 1, 1).atStartOfDay(UTC).toInstant(),
                LocalDate.of(2024, 1, 5).atStartOfDay(UTC).toInstant()
        );

        var symbolPrices = List.of(
                createSymbolPrice("ETH", "2024-01-01T10:00:00Z", "3000.00"),
                createSymbolPrice("ETH", "2024-01-02T10:00:00Z", "3100.00")
        );

        var response = new AggregatorResponse(
                symbolPrices,
                INCOMPLETE,
                period,
                "ETH"
        );

        // When
        var summary = evaluator.evaluate(response);

        // Then
        assertThat(summary.symbol()).isEqualTo("ETH");
        assertThat(summary.period()).isEqualTo(period);
        assertThat(summary.status()).isEqualTo(INCOMPLETE);
        assertThat(summary.minPrice()).isEqualTo(new BigDecimal("3000.00"));
        assertThat(summary.maxPrice()).isEqualTo(new BigDecimal("3100.00"));
        assertThat(summary.oldestPrice()).isEqualTo(new BigDecimal("3000.00"));
        assertThat(summary.newestPrice()).isEqualTo(new BigDecimal("3100.00"));
        assertThat(summary.normalizedRange()).isEqualTo(new BigDecimal("0.033333")); // (3100-3000)/3000
    }

    @Test
    void shouldHandleSinglePrice() {
        // Given
        var period = new Period(
                LocalDate.of(2024, 1, 1).atStartOfDay(UTC).toInstant(),
                LocalDate.of(2024, 1, 1).atStartOfDay(UTC).toInstant()
        );

        var symbolPrices = List.of(
                createSymbolPrice("DOGE", "2024-01-01T10:00:00Z", "0.50")
        );

        var response = new AggregatorResponse(
                symbolPrices,
                COMPLETE,
                period,
                "DOGE"
        );

        // When
        var summary = evaluator.evaluate(response);

        // Then
        assertThat(summary.minPrice()).isEqualTo(new BigDecimal("0.50"));
        assertThat(summary.maxPrice()).isEqualTo(new BigDecimal("0.50"));
        assertThat(summary.oldestPrice()).isEqualTo(new BigDecimal("0.50"));
        assertThat(summary.newestPrice()).isEqualTo(new BigDecimal("0.50"));
        assertThat(summary.normalizedRange()).isEqualTo(new BigDecimal("0.000000")); // Same min and max
    }


    @Test
    void shouldHandleUnorderedPrices() {
        // Given
        var period = new Period(
                LocalDate.of(2024, 1, 1).atStartOfDay(UTC).toInstant(),
                LocalDate.of(2024, 1, 3).atStartOfDay(UTC).toInstant()
        );

        var symbolPrices = List.of(
                createSymbolPrice("LTC", "2024-01-03T10:00:00Z", "110.00"), // Newest
                createSymbolPrice("LTC", "2024-01-01T10:00:00Z", "100.00"), // Oldest
                createSymbolPrice("LTC", "2024-01-02T10:00:00Z", "105.00")  // Middle
        );

        var response = new AggregatorResponse(
                symbolPrices,
                COMPLETE,
                period,
                "LTC"
        );

        // When
        var summary = evaluator.evaluate(response);

        // Then
        assertThat(summary.minPrice()).isEqualTo(new BigDecimal("100.00"));
        assertThat(summary.maxPrice()).isEqualTo(new BigDecimal("110.00"));
        assertThat(summary.oldestPrice()).isEqualTo(new BigDecimal("100.00"));
        assertThat(summary.newestPrice()).isEqualTo(new BigDecimal("110.00"));
        assertThat(summary.normalizedRange()).isEqualTo(new BigDecimal("0.100000")); // (110-100)/100
    }

    @ParameterizedTest
    @MethodSource("provideDifferentSymbols")
    void shouldHandleDifferentSymbols(String symbol, BigDecimal expectedPrice) {
        // Given
        var period = new Period(
                LocalDate.of(2024, 1, 1).atStartOfDay(UTC).toInstant(),
                LocalDate.of(2024, 1, 1).atStartOfDay(UTC).toInstant()
        );

        var symbolPrices = List.of(
                createSymbolPrice(symbol, "2024-01-01T10:00:00Z", expectedPrice.toString())
        );

        var response = new AggregatorResponse(
                symbolPrices,
                COMPLETE,
                period,
                symbol
        );

        // When
        var summary = evaluator.evaluate(response);

        // Then
        assertThat(summary.symbol()).isEqualTo(symbol);
        assertThat(summary.minPrice()).isEqualTo(expectedPrice);
        assertThat(summary.maxPrice()).isEqualTo(expectedPrice);
        assertThat(summary.oldestPrice()).isEqualTo(expectedPrice);
        assertThat(summary.newestPrice()).isEqualTo(expectedPrice);
    }

    @Test
    void shouldHandleZeroPrice() {
        // Given
        var period = new Period(
                LocalDate.of(2024, 1, 1).atStartOfDay(UTC).toInstant(),
                LocalDate.of(2024, 1, 1).atStartOfDay(UTC).toInstant()
        );

        var symbolPrices = List.of(
                createSymbolPrice("TEST", "2024-01-01T10:00:00Z", "0.00")
        );

        var response = new AggregatorResponse(
                symbolPrices,
                COMPLETE,
                period,
                "TEST"
        );

        // When
        var summary = evaluator.evaluate(response);

        // Then
        assertThat(summary.minPrice()).isEqualTo(new BigDecimal("0.00"));
        assertThat(summary.maxPrice()).isEqualTo(new BigDecimal("0.00"));
        assertThat(summary.oldestPrice()).isEqualTo(new BigDecimal("0.00"));
        assertThat(summary.newestPrice()).isEqualTo(new BigDecimal("0.00"));
        assertThat(summary.normalizedRange()).isEqualTo(ZERO);
    }

    private static Stream<Arguments> provideDifferentSymbols() {
        return Stream.of(
                Arguments.of("BTC", new BigDecimal("50000.00")),
                Arguments.of("ETH", new BigDecimal("3000.00")),
                Arguments.of("DOGE", new BigDecimal("0.50")),
                Arguments.of("LTC", new BigDecimal("100.00")),
                Arguments.of("XRP", new BigDecimal("0.50"))
        );
    }

    private SymbolPrice createSymbolPrice(String symbol, String timestamp, String price) {
        return new SymbolPrice(
                symbol,
                Instant.parse(timestamp),
                new BigDecimal(price)
        );
    }
}
