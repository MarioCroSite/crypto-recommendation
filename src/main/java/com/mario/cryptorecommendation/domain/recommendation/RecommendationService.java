package com.mario.cryptorecommendation.domain.recommendation;

import com.mario.cryptorecommendation.domain.ingestion.symbolconfig.SymbolConfigRepository;
import com.mario.cryptorecommendation.domain.ingestion.symbolprice.SymbolPriceRepository;
import com.mario.cryptorecommendation.domain.ingestion.symbolpricesummary.SymbolPriceSummary;
import com.mario.cryptorecommendation.domain.ingestion.symbolpricesummary.SymbolPriceSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final SymbolPriceRepository symbolPriceRepository;
    private final SymbolPriceSummaryRepository symbolPriceSummaryRepository;
    private final SymbolConfigRepository symbolConfigRepository;

    @Cacheable("summaryPrices")
    public List<SymbolPriceSummary> getLatestSummaryPrices() {
        return symbolPriceSummaryRepository.findLatestSummaryPrices();
    }

    public String getSymbolWithHighestNormalizedRangeInDay(LocalDate date) {
        return symbolPriceRepository.findSymbolWithHighestNormalizedRangeInDay(date);
    }

    public Optional<SymbolPriceSummary> getLatestSymbolPriceForSymbol(String symbol) {
        var symbolConfig = symbolConfigRepository.findBySymbol(symbol)
                .orElseThrow(() -> new SymbolNotSupportedException("Symbol not supported: " + symbol));

        return symbolPriceSummaryRepository.findLatestSummaryPricesBySymbol(symbolConfig.symbol());
    }
}
