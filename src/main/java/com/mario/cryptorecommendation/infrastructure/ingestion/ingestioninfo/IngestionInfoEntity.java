package com.mario.cryptorecommendation.infrastructure.ingestion.ingestioninfo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "ingestion_info")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
public class IngestionInfoEntity {

    @Id
    @Column(name = "ingestion_id", length = 36)
    private String ingestionId;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "symbol", nullable = false, length = 20)
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(name = "extension_type", nullable = false, length = 10)
    private ExtensionType extensionType;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Column(name = "number_of_records", nullable = false)
    private Integer numberOfRecords;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private IngestionStatus status;
}
