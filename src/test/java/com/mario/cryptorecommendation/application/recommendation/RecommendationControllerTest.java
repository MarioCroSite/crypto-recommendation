package com.mario.cryptorecommendation.application.recommendation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mario.cryptorecommendation.domain.ingestion.Period;
import com.mario.cryptorecommendation.domain.ingestion.aggregator.AggregatedStatus;
import com.mario.cryptorecommendation.domain.ingestion.symbolpricesummary.SymbolPriceSummary;
import com.mario.cryptorecommendation.domain.recommendation.NoDataFoundException;
import com.mario.cryptorecommendation.domain.recommendation.RecommendationService;
import com.mario.cryptorecommendation.domain.recommendation.SymbolNotSupportedException;
import com.mario.cryptorecommendation.domain.utils.NormalizedRangeCalculator;
import com.mario.cryptorecommendation.infrastructure.config.RateLimitingFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = RecommendationController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = RateLimitingFilter.class))
public class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RecommendationService recommendationService;

    @MockitoBean
    private RecommendationDtoMapper mapper;

    private static final String VALID_SYMBOL = "BTC";
    private static final LocalDate VALID_DATE = LocalDate.of(2025, 9, 1);

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void shouldGetAllSymbolsByNormalizedRangeDescending() throws Exception {
        // Given
        var symbolPriceSummary1 = createSymbolPriceSummary("BTC", new BigDecimal("50000"), new BigDecimal("55000"));
        var symbolPriceSummary2 = createSymbolPriceSummary("ETH", new BigDecimal("3000"), new BigDecimal("3200"));

        var dto1 = createSymbolPriceSummaryDto("BTC", new BigDecimal("50000"), new BigDecimal("55000"));
        var dto2 = createSymbolPriceSummaryDto("ETH", new BigDecimal("3000"), new BigDecimal("3200"));

        when(recommendationService.getLatestSummaryPrices()).thenReturn(List.of(symbolPriceSummary1, symbolPriceSummary2));
        when(mapper.toDto(symbolPriceSummary1)).thenReturn(dto1);
        when(mapper.toDto(symbolPriceSummary2)).thenReturn(dto2);

        // When
        var result = mockMvc.perform(get("/api/v1/recommendation/normalized-range")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Then
        var symbolPriceSummaryResponse = objectMapper.readValue(result, new TypeReference<List<SymbolPriceSummaryDto>>() {});
        verifyNormalizedRangeResponse(symbolPriceSummaryResponse, List.of(dto1, dto2));

        verify(recommendationService, times(1)).getLatestSummaryPrices();
        verify(mapper, times(1)).toDto(symbolPriceSummary1);
        verify(mapper, times(1)).toDto(symbolPriceSummary2);
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void shouldReturnEmptyListWhenNoSymbolsAvailable() throws Exception {
        // Given
        when(recommendationService.getLatestSummaryPrices()).thenReturn(List.of());

        // When & Then
        var result = mockMvc.perform(get("/api/v1/recommendation/normalized-range")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var symbolPriceSummaryResponse = objectMapper.readValue(result, new TypeReference<List<SymbolPriceSummaryDto>>() {});
        assertThat(symbolPriceSummaryResponse).isEmpty();

        verify(recommendationService, times(1)).getLatestSummaryPrices();
        verify(mapper, never()).toDto(any());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void shouldGetSymbolWithHighestNormalizedRangeInDay() throws Exception {
        // Given
        String expectedSymbol = "BTC";
        when(recommendationService.getSymbolWithHighestNormalizedRangeInDay(VALID_DATE)).thenReturn(expectedSymbol);

        // When & Then
        var result = mockMvc.perform(get("/api/v1/recommendation/normalized-range/highest")
                        .param("date", VALID_DATE.toString())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).contains(expectedSymbol);
        verify(recommendationService, times(1)).getSymbolWithHighestNormalizedRangeInDay(VALID_DATE);
    }

    @ParameterizedTest
    @ValueSource(strings = {"2024-09-01", "2023-12-31", "2025-01-15"})
    @WithMockUser(username = "admin", password = "admin")
    void shouldHandleDifferentValidDateFormats(String dateString) throws Exception {
        // Given
        when(recommendationService.getSymbolWithHighestNormalizedRangeInDay(any(LocalDate.class))).thenReturn("BTC");

        // When & Then
        mockMvc.perform(get("/api/v1/recommendation/normalized-range/highest")
                        .param("date", dateString)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(recommendationService, times(1)).getSymbolWithHighestNormalizedRangeInDay(any(LocalDate.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-date", "01-15-2025", "15/01/2024", ""})
    @WithMockUser(username = "admin", password = "admin")
    void shouldReturnBadRequestForInvalidDateFormats(String invalidDate) throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/recommendation/normalized-range/highest")
                        .param("date", invalidDate)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin",  password = "admin")
    void shouldReturnNotFoundWhenNoDataFoundForDate() throws Exception {
        // Given
        when(recommendationService.getSymbolWithHighestNormalizedRangeInDay(VALID_DATE))
                .thenThrow(new NoDataFoundException("No symbol prices found for the given day: " + VALID_DATE));

        // When & Then
        mockMvc.perform(get("/api/v1/recommendation/normalized-range/highest")
                        .param("date", VALID_DATE.toString())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(recommendationService, times(1)).getSymbolWithHighestNormalizedRangeInDay(VALID_DATE);
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void shouldGetCryptoStatsForValidSymbol() throws Exception {
        // Given
        var symbolPriceSummary = createSymbolPriceSummary(VALID_SYMBOL, new BigDecimal("50000"), new BigDecimal("55000"));
        var dto = createSymbolPriceSummaryDto(VALID_SYMBOL, new BigDecimal("50000"), new BigDecimal("55000"));

        when(recommendationService.getLatestSymbolPriceForSymbol(VALID_SYMBOL)).thenReturn(Optional.of(symbolPriceSummary));
        when(mapper.toDto(symbolPriceSummary)).thenReturn(dto);

        // When
        var result = mockMvc.perform(get("/api/v1/recommendation/summary/{symbol}/info", VALID_SYMBOL)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Then
        var responseDto = objectMapper.readValue(result, SymbolPriceSummaryDto.class);
        verifySingleSymbolResponse(responseDto, dto);

        verify(recommendationService, times(1)).getLatestSymbolPriceForSymbol(VALID_SYMBOL);
        verify(mapper, times(1)).toDto(symbolPriceSummary);
    }

    @ParameterizedTest
    @ValueSource(strings = {"BTC", "ETH", "DOGE", "LTC", "XRP"})
    @WithMockUser(username = "admin", password = "admin")
    void shouldHandleDifferentValidSymbols(String symbol) throws Exception {
        // Given
        var symbolPriceSummary = createSymbolPriceSummary(symbol, new BigDecimal("100"), new BigDecimal("110"));
        var dto = createSymbolPriceSummaryDto(symbol, new BigDecimal("100"), new BigDecimal("110"));

        when(recommendationService.getLatestSymbolPriceForSymbol(symbol)).thenReturn(Optional.of(symbolPriceSummary));
        when(mapper.toDto(symbolPriceSummary)).thenReturn(dto);

        // When & Then
        var result = mockMvc.perform(get("/api/v1/recommendation/summary/{symbol}/info", symbol)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var responseDto = objectMapper.readValue(result, SymbolPriceSummaryDto.class);
        assertThat(responseDto.symbol()).isEqualTo(symbol);
        verify(recommendationService, times(1)).getLatestSymbolPriceForSymbol(symbol);
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void shouldReturnNotFoundWhenSymbolNotFound() throws Exception {
        // Given
        when(recommendationService.getLatestSymbolPriceForSymbol(VALID_SYMBOL)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/recommendation/summary/{symbol}/info", VALID_SYMBOL)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(recommendationService, times(1)).getLatestSymbolPriceForSymbol(VALID_SYMBOL);
        verify(mapper, never()).toDto(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"INVALID", "UNKNOWN", "FAKE"})
    @WithMockUser(username = "admin", password = "admin")
    void shouldReturnBadRequestForUnsupportedSymbols(String unsupportedSymbol) throws Exception {
        // Given
        when(recommendationService.getLatestSymbolPriceForSymbol(unsupportedSymbol))
                .thenThrow(new SymbolNotSupportedException("Symbol not supported: " + unsupportedSymbol));

        // When & Then
        mockMvc.perform(get("/api/v1/recommendation/summary/{symbol}/info", unsupportedSymbol)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(recommendationService, times(1)).getLatestSymbolPriceForSymbol(unsupportedSymbol);
    }

    @ParameterizedTest
    @MethodSource("provideUnauthenticatedEndpoints")
    void shouldReturnUnauthorizedForAllEndpointsWhenNotAuthenticated(String endpoint) throws Exception {
        // When & Then
        mockMvc.perform(get(endpoint)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    private static Stream<Arguments> provideUnauthenticatedEndpoints() {
        return Stream.of(
                Arguments.of("/api/v1/recommendation/normalized-range"),
                Arguments.of("/api/v1/recommendation/normalized-range/highest?date=2025-09-01"),
                Arguments.of("/api/v1/recommendation/summary/BTC/info")
        );
    }

    private SymbolPriceSummary createSymbolPriceSummary(String symbol, BigDecimal minPrice, BigDecimal maxPrice) {
        return new SymbolPriceSummary(
                symbol,
                new Period(
                        java.time.Instant.now().minusSeconds(3600),
                        java.time.Instant.now()
                ),
                AggregatedStatus.COMPLETE,
                minPrice,
                maxPrice,
                minPrice,
                maxPrice,
                NormalizedRangeCalculator.calculate(minPrice, maxPrice)
        );
    }

    private SymbolPriceSummaryDto createSymbolPriceSummaryDto(String symbol, BigDecimal minPrice, BigDecimal maxPrice) {
        return new SymbolPriceSummaryDto(
                symbol,
                minPrice,
                maxPrice,
                minPrice,
                maxPrice,
                NormalizedRangeCalculator.calculate(minPrice, maxPrice)
        );
    }

    private void verifyNormalizedRangeResponse(List<SymbolPriceSummaryDto> responseDtos, List<SymbolPriceSummaryDto> expectedDtos) {
        assertThat(responseDtos).hasSize(expectedDtos.size());

        for (int i = 0; i < expectedDtos.size(); i++) {
            SymbolPriceSummaryDto responseDto = responseDtos.get(i);
            SymbolPriceSummaryDto expectedDto = expectedDtos.get(i);

            assertThat(responseDto.symbol()).isEqualTo(expectedDto.symbol());
            assertThat(responseDto.minPrice()).isEqualTo(expectedDto.minPrice());
            assertThat(responseDto.maxPrice()).isEqualTo(expectedDto.maxPrice());
            assertThat(responseDto.oldestPrice()).isEqualTo(expectedDto.oldestPrice());
            assertThat(responseDto.newestPrice()).isEqualTo(expectedDto.newestPrice());
            assertThat(responseDto.normalizedRange()).isEqualTo(expectedDto.normalizedRange());
        }
    }

    private void verifySingleSymbolResponse(SymbolPriceSummaryDto responseDto, SymbolPriceSummaryDto expectedDto) {
        assertThat(responseDto.symbol()).isEqualTo(expectedDto.symbol());
        assertThat(responseDto.minPrice()).isEqualTo(expectedDto.minPrice());
        assertThat(responseDto.maxPrice()).isEqualTo(expectedDto.maxPrice());
        assertThat(responseDto.oldestPrice()).isEqualTo(expectedDto.oldestPrice());
        assertThat(responseDto.newestPrice()).isEqualTo(expectedDto.newestPrice());
        assertThat(responseDto.normalizedRange()).isEqualTo(expectedDto.normalizedRange());
    }
}
