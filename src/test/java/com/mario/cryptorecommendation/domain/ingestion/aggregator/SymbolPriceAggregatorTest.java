package com.mario.cryptorecommendation.domain.ingestion.aggregator;

import com.mario.cryptorecommendation.domain.ingestion.Period;
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

import static com.mario.cryptorecommendation.domain.ingestion.aggregator.AggregatedStatus.COMPLETE;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;


class SymbolPriceAggregatorTest {

    private SymbolPriceAggregator aggregator;

    @BeforeEach
    void setUp() {
        aggregator = new SymbolPriceAggregator();
    }

    @Test
    void shouldAggregateCompleteData() {
        // Given
        var period = new Period(
                LocalDate.of(2024, 1, 1).atStartOfDay(UTC).toInstant(),
                LocalDate.of(2024, 1, 3).atStartOfDay(UTC).toInstant()
        );

        var existingPrices = List.of(
                createSymbolPrice("BTC", "2024-01-01T10:00:00Z", "50000"),
                createSymbolPrice("BTC", "2024-01-02T10:00:00Z", "51000")
        );

        var newPrices = List.of(
                createSymbolPrice("BTC", "2024-01-03T10:00:00Z", "52000")
        );

        var request = AggregatorRequest.builder()
                .existingSymbolPrices(existingPrices)
                .newSymbolPrices(newPrices)
                .period(period)
                .build();

        // When
        var response = aggregator.aggregate(request);

        // Then
        assertThat(response.status()).isEqualTo(COMPLETE);
        assertThat(response.symbol()).isEqualTo("BTC");
        assertThat(response.period()).isEqualTo(period);
        assertThat(response.aggregatedSymbolPrices()).hasSize(3);
        assertThat(response.aggregatedSymbolPrices()).isSortedAccordingTo(
                (sp1, sp2) -> sp2.createdAt().compareTo(sp1.createdAt())
        );
    }

    @Test
    void shouldAggregateIncompleteData() {
        // Given
        var period = new Period(
                LocalDate.of(2024, 1, 1).atStartOfDay(UTC).toInstant(),
                LocalDate.of(2024, 1, 5).atStartOfDay(UTC).toInstant()
        );

        var existingPrices = List.of(
                createSymbolPrice("ETH", "2024-01-01T10:00:00Z", "3000"),
                createSymbolPrice("ETH", "2024-01-02T10:00:00Z", "3100")
        );

        var newPrices = List.of(
                createSymbolPrice("ETH", "2024-01-03T10:00:00Z", "3200")
        );

        var request = AggregatorRequest.builder()
                .existingSymbolPrices(existingPrices)
                .newSymbolPrices(newPrices)
                .period(period)
                .build();

        // When
        var response = aggregator.aggregate(request);

        // Then
        assertThat(response.status()).isEqualTo(AggregatedStatus.INCOMPLETE);
        assertThat(response.symbol()).isEqualTo("ETH");
        assertThat(response.aggregatedSymbolPrices()).hasSize(3);
    }

    @Test
    void shouldDetectConflicts() {
        // Given
        var period = new Period(
                LocalDate.of(2024, 1, 1).atStartOfDay(UTC).toInstant(),
                LocalDate.of(2024, 1, 3).atStartOfDay(UTC).toInstant()
        );

        var conflictingPrice = createSymbolPrice("DOGE", "2024-01-02T10:00:00Z", "0.50");

        var existingPrices = List.of(
                createSymbolPrice("DOGE", "2024-01-01T10:00:00Z", "0.45"),
                conflictingPrice
        );

        var newPrices = List.of(
                conflictingPrice, // Same price as existing
                createSymbolPrice("DOGE", "2024-01-03T10:00:00Z", "0.55")
        );

        var request = AggregatorRequest.builder()
                .existingSymbolPrices(existingPrices)
                .newSymbolPrices(newPrices)
                .period(period)
                .build();

        // When
        var response = aggregator.aggregate(request);

        // Then
        assertThat(response.status()).isEqualTo(AggregatedStatus.CONFLICT);
        assertThat(response.symbol()).isEqualTo("DOGE");
        assertThat(response.aggregatedSymbolPrices()).isNull();
    }

    @Test
    void shouldMergePricesWithoutDuplicates() {
        // Given
        var period = new Period(
                LocalDate.of(2024, 1, 1).atStartOfDay(UTC).toInstant(),
                LocalDate.of(2024, 1, 2).atStartOfDay(UTC).toInstant()
        );

        var existingPrices = List.of(
                createSymbolPrice("LTC", "2024-01-01T10:00:00Z", "100")
        );

        var newPrices = List.of(
                createSymbolPrice("LTC", "2024-01-01T14:00:00Z", "105"), // Different timestamp
                createSymbolPrice("LTC", "2024-01-02T10:00:00Z", "110")
        );

        var request = AggregatorRequest.builder()
                .existingSymbolPrices(existingPrices)
                .newSymbolPrices(newPrices)
                .period(period)
                .build();

        // When
        var response = aggregator.aggregate(request);

        // Then
        assertThat(response.status()).isEqualTo(AggregatedStatus.COMPLETE);
        assertThat(response.aggregatedSymbolPrices()).hasSize(3);
        assertThat(response.aggregatedSymbolPrices()).isSortedAccordingTo(
                (sp1, sp2) -> sp2.createdAt().compareTo(sp1.createdAt())
        );
    }

    @ParameterizedTest
    @MethodSource("provideSymbolScenarios")
    void shouldAggregateDifferentSymbols(String symbol, BigDecimal expectedPrice) {
        // Given
        var period = new Period(
                LocalDate.of(2024, 1, 1).atStartOfDay(UTC).toInstant(),
                LocalDate.of(2024, 1, 1).atStartOfDay(UTC).toInstant()
        );

        var newPrices = List.of(
                createSymbolPrice(symbol, "2024-01-01T10:00:00Z", expectedPrice.toString())
        );

        var request = AggregatorRequest.builder()
                .existingSymbolPrices(List.of())
                .newSymbolPrices(newPrices)
                .period(period)
                .build();

        // When
        AggregatorResponse response = aggregator.aggregate(request);

        // Then
        assertThat(response.status()).isEqualTo(COMPLETE);
        assertThat(response.symbol()).isEqualTo(symbol);
        assertThat(response.aggregatedSymbolPrices()).hasSize(1);
        assertThat(response.aggregatedSymbolPrices().getFirst().price()).isEqualTo(expectedPrice);
    }

    @Test
    void shouldHandleEmptyExistingPrices() {
        // Given
        var period = new Period(
                LocalDate.of(2024, 1, 1).atStartOfDay(UTC).toInstant(),
                LocalDate.of(2024, 1, 1).atStartOfDay(UTC).toInstant()
        );

        var newPrices = List.of(
                createSymbolPrice("XRP", "2024-01-01T10:00:00Z", "0.50")
        );

        var request = AggregatorRequest.builder()
                .existingSymbolPrices(List.of())
                .newSymbolPrices(newPrices)
                .period(period)
                .build();

        // When
        AggregatorResponse response = aggregator.aggregate(request);

        // Then
        assertThat(response.status()).isEqualTo(COMPLETE);
        assertThat(response.symbol()).isEqualTo("XRP");
        assertThat(response.aggregatedSymbolPrices()).hasSize(1);
    }

    private static Stream<Arguments> provideSymbolScenarios() {
        return Stream.of(
                Arguments.of("BTC", new BigDecimal("50000")),
                Arguments.of("ETH", new BigDecimal("3000")),
                Arguments.of("DOGE", new BigDecimal("0.50")),
                Arguments.of("LTC", new BigDecimal("100")),
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