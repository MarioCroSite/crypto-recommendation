package com.mario.cryptorecommendation.application.recommendation;

import com.mario.cryptorecommendation.domain.recommendation.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequestMapping("/api/v1/recommendation")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final RecommendationDtoMapper mapper;

    // Exposes an endpoint that will return a descending sorted list of all the cryptos, comparing the normalized range (i.e. (max-min)/min)
    @GetMapping("/normalized-range")
    public ResponseEntity<List<SymbolPriceSummaryDto>> getAllSymbolsByNormalizedRangeDescending() {
        return ok(recommendationService.getLatestSummaryPrices().stream().map(mapper::toDto).toList());
    }

    // Exposes an endpoint that will return the crypto with the highest normalized range for a specific day
    @GetMapping("/normalized-range/highest")
    public ResponseEntity<String> getSymbolWithHighestNormalizedRangeInDay(
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date) {
        return ok(recommendationService.getSymbolWithHighestNormalizedRangeInDay(date));
    }

    // Exposes an endpoint that will return the oldest/newest/min/max values for a requested crypto
    @GetMapping("/summary/{symbol}/info")
    public ResponseEntity<SymbolPriceSummaryDto> getCryptoStats(@PathVariable String symbol) {
       var latestSymbolPrice =  recommendationService.getLatestSymbolPriceForSymbol(symbol);

        return latestSymbolPrice.map(symbolPrice -> ResponseEntity.ok(mapper.toDto(symbolPrice)))
                .orElseGet(() -> ResponseEntity.status(NOT_FOUND).build());
    }
}
