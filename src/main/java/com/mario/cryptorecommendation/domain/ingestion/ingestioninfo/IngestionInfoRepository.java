package com.mario.cryptorecommendation.domain.ingestion.ingestioninfo;


public interface IngestionInfoRepository {

    IngestionInfo save(IngestionInfo ingestionInfo);

    IngestionInfo ingestionSuccessful(IngestionInfo ingestionInfo, int numberOfRecords);

    IngestionInfo ingestionFailed(IngestionInfo ingestionInfo);
}
