package com.mario.cryptorecommendation.infrastructure.ingestion.ingestioninfo;

import com.mario.cryptorecommendation.domain.ingestion.ingestioninfo.IngestionInfo;
import com.mario.cryptorecommendation.domain.ingestion.ingestioninfo.IngestionInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

import static com.mario.cryptorecommendation.infrastructure.ingestion.ingestioninfo.IngestionStatus.COMPLETED;
import static com.mario.cryptorecommendation.infrastructure.ingestion.ingestioninfo.IngestionStatus.FAILED;

@Component
@RequiredArgsConstructor
public class IngestionInfoRepositoryAdapter implements IngestionInfoRepository {

    private final IngestionInfoJpaRepository ingestionInfoJpaRepository;
    private final IngestionInfoMapper ingestionInfoMapper;

    @Override
    public IngestionInfo save(IngestionInfo ingestionInfo) {
        var entity = ingestionInfoMapper.toEntity(ingestionInfo);
        return ingestionInfoMapper.toDomain(ingestionInfoJpaRepository.save(entity));
    }

    @Override
    public IngestionInfo ingestionSuccessful(IngestionInfo ingestionInfo, int numberOfRecords) {
        var entity = ingestionInfoMapper.toEntity(ingestionInfo);
        var updatedEntity = entity.toBuilder()
                .status(COMPLETED)
                .numberOfRecords(numberOfRecords)
                .endTime(Instant.now())
                .build();

        return ingestionInfoMapper.toDomain(ingestionInfoJpaRepository.save(updatedEntity));
    }

    @Override
    public IngestionInfo ingestionFailed(IngestionInfo ingestionInfo) {
        var entity = ingestionInfoMapper.toEntity(ingestionInfo);
        var updatedEntity = entity.toBuilder()
                .status(FAILED)
                .endTime(Instant.now())
                .build();
        return ingestionInfoMapper.toDomain(ingestionInfoJpaRepository.save(updatedEntity));
    }

}
