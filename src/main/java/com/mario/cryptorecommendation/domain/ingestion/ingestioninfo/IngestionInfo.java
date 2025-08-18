package com.mario.cryptorecommendation.domain.ingestion.ingestioninfo;

import com.mario.cryptorecommendation.domain.utils.file.ExtensionType;
import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record IngestionInfo(
        String ingestionId,
        String filePath,
        String symbol,
        ExtensionType extensionType,
        Instant startTime,
        Instant endTime,
        int numberOfRecords,
        IngestionStatus status
) {

}
