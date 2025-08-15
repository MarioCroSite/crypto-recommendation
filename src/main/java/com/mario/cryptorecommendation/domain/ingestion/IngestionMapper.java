package com.mario.cryptorecommendation.domain.ingestion;

import com.mario.cryptorecommendation.domain.ingestion.reader.Statement;
import com.mario.cryptorecommendation.domain.ingestion.symbolprice.SymbolPrice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface IngestionMapper {

    @Mapping(target = "createdAt", source = "timestamp")
    SymbolPrice toSymbolPrice(Statement statement);

    List<SymbolPrice> toSymbolPriceList(List<Statement> statements);
}
