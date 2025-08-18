package com.mario.cryptorecommendation.domain.ingestion.ingestioninfo;

import com.mario.cryptorecommendation.domain.ingestion.symbolpricesummary.SymbolPriceSummary;

public interface IngestionInfoRepository {

    IngestionInfo save(IngestionInfo ingestionInfo);

    IngestionInfo ingestionSuccessful(IngestionInfo ingestionInfo, int numberOfRecords);

    IngestionInfo ingestionFailed(IngestionInfo ingestionInfo);
}
