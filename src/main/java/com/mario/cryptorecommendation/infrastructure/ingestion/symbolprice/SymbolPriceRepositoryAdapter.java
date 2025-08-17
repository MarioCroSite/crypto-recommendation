package com.mario.cryptorecommendation.infrastructure.ingestion.symbolprice;

import com.mario.cryptorecommendation.domain.ingestion.symbolprice.SymbolPrice;
import com.mario.cryptorecommendation.domain.ingestion.symbolprice.SymbolPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.StreamSupport;

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
}
