package com.mario.cryptorecommendation.application.recommendation;

import com.mario.cryptorecommendation.domain.ingestion.Period;
import com.mario.cryptorecommendation.domain.ingestion.aggregator.AggregatedStatus;
import com.mario.cryptorecommendation.domain.ingestion.symbolpricesummary.SymbolPriceSummary;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mapstruct.factory.Mappers.getMapper;

public class RecommendationDtoMapperTest {

    private final RecommendationDtoMapper mapper = getMapper(RecommendationDtoMapper.class);

    @ParameterizedTest
    @ValueSource(strings = {"BTC", "ETH", "DOGE", "LTC", "XRP"})
    void shouldMapDifferentSymbols(String symbol) {
        // Given
        var period = new Period(Instant.now().minusSeconds(3600), Instant.now());
        var symbolPriceSummary = new SymbolPriceSummary(
                symbol,
                period,
                AggregatedStatus.COMPLETE,
                new BigDecimal("100"),
                new BigDecimal("110"),
                new BigDecimal("100"),
                new BigDecimal("110"),
                new BigDecimal("0.100000")
        );

        // When
        var result = mapper.toDto(symbolPriceSummary);

        // Then
        assertThat(result.symbol()).isEqualTo(symbol);
        assertThat(result.minPrice()).isEqualTo(new BigDecimal("100"));
        assertThat(result.maxPrice()).isEqualTo(new BigDecimal("110"));
        assertThat(result.normalizedRange()).isEqualTo(new BigDecimal("0.100000"));
    }
}
