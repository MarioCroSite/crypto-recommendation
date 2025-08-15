package com.mario.cryptorecommendation.domain.ingestion;

import com.mario.cryptorecommendation.domain.utils.file.ExtensionType;
import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record IngestionDetails(
        String ingestionId,
        String filePath,
        String symbol,
        ExtensionType extensionType,
        Instant startTime,
        Instant endTime
) {

}
