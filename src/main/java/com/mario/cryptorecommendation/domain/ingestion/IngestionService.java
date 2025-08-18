package com.mario.cryptorecommendation.domain.ingestion;

import com.mario.cryptorecommendation.domain.ingestion.aggregator.AggregatorRequest;
import com.mario.cryptorecommendation.domain.ingestion.aggregator.AggregatorResponse;
import com.mario.cryptorecommendation.domain.ingestion.aggregator.SymbolPriceAggregator;
import com.mario.cryptorecommendation.domain.ingestion.ingestioninfo.IngestionInfo;
import com.mario.cryptorecommendation.domain.ingestion.ingestioninfo.IngestionInfoRepository;
import com.mario.cryptorecommendation.domain.ingestion.symbollock.FailedToLockSymbol;
import com.mario.cryptorecommendation.domain.ingestion.symbolpricesummary.SymbolPriceEvaluator;
import com.mario.cryptorecommendation.domain.ingestion.symbolconfig.SymbolConfigRepository;
import com.mario.cryptorecommendation.domain.ingestion.symbollock.SymbolLockRepository;
import com.mario.cryptorecommendation.domain.ingestion.reader.FileReaderFactory;
import com.mario.cryptorecommendation.domain.ingestion.reader.Statement;
import com.mario.cryptorecommendation.domain.ingestion.symbolprice.SymbolPriceRepository;
import com.mario.cryptorecommendation.domain.ingestion.symbolpricesummary.SymbolPriceSummaryRepository;
import com.mario.cryptorecommendation.domain.utils.file.FileInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.mario.cryptorecommendation.domain.ingestion.ingestioninfo.IngestionStatus.IN_PROGRESS;
import static com.mario.cryptorecommendation.domain.ingestion.aggregator.AggregatedStatus.CONFLICT;
import static com.mario.cryptorecommendation.domain.utils.file.FileInfoUtil.getFilesFromDirectoryLocation;

@Service
@Slf4j
public class IngestionService {
    private static final int THREAD_POOL_SIZE = 10;

    private final ExecutorService executorService;
    private final SymbolLockRepository symbolLockRepository;
    private final SymbolConfigRepository symbolConfigRepository;
    private final SymbolPriceRepository symbolPriceRepository;
    private final FileReaderFactory fileReaderFactory;
    private final SymbolPriceAggregator symbolPriceAggregator;
    private final IngestionMapper ingestionMapper;
    private final SymbolPriceEvaluator symbolPriceEvaluator;
    private final SymbolPriceSummaryRepository symbolPriceSummaryRepository;
    private final IngestionInfoRepository ingestionInfoRepository;

    public IngestionService(SymbolLockRepository symbolLockRepository,
                            FileReaderFactory fileReaderFactory,
                            SymbolConfigRepository symbolConfigRepository,
                            SymbolPriceRepository symbolPriceRepository,
                            SymbolPriceAggregator symbolPriceAggregator,
                            IngestionMapper ingestionMapper,
                            SymbolPriceEvaluator symbolPriceEvaluator,
                            SymbolPriceSummaryRepository symbolPriceSummaryRepository,
                            IngestionInfoRepository ingestionInfoRepository) {
        this.executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        this.symbolLockRepository = symbolLockRepository;
        this.fileReaderFactory = fileReaderFactory;
        this.symbolConfigRepository = symbolConfigRepository;
        this.symbolPriceRepository = symbolPriceRepository;
        this.symbolPriceAggregator = symbolPriceAggregator;
        this.ingestionMapper = ingestionMapper;
        this.symbolPriceEvaluator = symbolPriceEvaluator;
        this.symbolPriceSummaryRepository = symbolPriceSummaryRepository;
        this.ingestionInfoRepository = ingestionInfoRepository;
    }

    public void startIngestion(String directoryLocation) {
        var fileInfos = getFilesFromDirectoryLocation(directoryLocation);
        fileInfos.forEach(fileInfo -> executorService.submit(() -> ingest(fileInfo)));
    }


    private void ingest(FileInfo fileInfo) {
        var ingestionInfo = ingestionStarted(fileInfo);

        // Validate we can ingest the symbol and lock it
        lockSymbol(fileInfo.symbol());

        // save to ingestion repository
        ingestionInfoRepository.save(ingestionInfo);

        // statements ordered by timestamp descending
        var statements = getStatementsFromFile(fileInfo);
        var aggregatedResult = getAggregatedData(statements, fileInfo);

        var symbolPriceSummary = symbolPriceEvaluator.evaluate(aggregatedResult);
        if (symbolPriceSummary.status() == CONFLICT) {
            symbolPriceSummaryRepository.save(symbolPriceSummary);
            throw new RuntimeException("Aggregated data for symbol %s has conflicts. Ingestion failed.".formatted(fileInfo.symbol()));
        }

        // Store symbol prices in the database
        symbolPriceRepository.saveAll(aggregatedResult.aggregatedSymbolPrices());

        // Save / update summary in the database
        symbolPriceSummaryRepository.save(symbolPriceSummary);

        // Update ingestion information in the database
        ingestionInfoRepository.ingestionSuccessful(ingestionInfo, aggregatedResult.aggregatedSymbolPrices().size());

        unlockSymbol(fileInfo.symbol());
    }

    private IngestionInfo ingestionStarted(FileInfo fileInfo) {
        return IngestionInfo.builder()
                .ingestionId(UUID.randomUUID().toString())
                .filePath(fileInfo.filePath())
                .symbol(fileInfo.symbol())
                .extensionType(fileInfo.extensionType())
                .startTime(Instant.now())
                .numberOfRecords(0)
                .status(IN_PROGRESS)
                .build();
    }

    private void lockSymbol(String symbol) {
        var symbolOpt = symbolLockRepository.findBySymbol(symbol);
        if (symbolOpt.isEmpty()) {
            throw new FailedToLockSymbol("Symbol %s is not available in our system.".formatted(symbol));
        }

        if (symbolOpt.get().locked()) {
            throw new FailedToLockSymbol("Symbol %s is already locked.".formatted(symbol));
        }

        var lockSymbol = symbolLockRepository.lockSymbol(symbol);
        if (!lockSymbol) {
            throw new FailedToLockSymbol("Failed to lock symbol %s.".formatted(symbol));
        }
    }

    private List<Statement> getStatementsFromFile(FileInfo fileInfo) {
        var statements = fileReaderFactory.getFileReader(fileInfo.extensionType()).readStatements(fileInfo);
        if (statements.isEmpty()) {
            log.error("No statements found in file: {}", fileInfo.filePath());
            throw new RuntimeException("No statements found in file: " + fileInfo.filePath());
        }
        return statements;
    }

    private AggregatorResponse getAggregatedData(List<Statement> statements, FileInfo fileInfo) {
        var period = getPeriod(statements.getFirst(), fileInfo);
        var existingSymbolPrices = symbolPriceRepository.findBySymbolAndDateTimeRange(fileInfo.symbol(), period.start(), period.end());
        var newSymbolPrices = ingestionMapper.toSymbolPriceList(statements);

        return symbolPriceAggregator.aggregate(AggregatorRequest.builder()
                .newSymbolPrices(newSymbolPrices)
                .existingSymbolPrices(existingSymbolPrices)
                .period(period)
                .build());
    }

    private Period getPeriod(Statement lastStatement, FileInfo fileInfo) {
        var symbolConfig = symbolConfigRepository.findBySymbol(fileInfo.symbol())
                .orElseThrow(() -> new RuntimeException("Symbol configuration not found for: " + fileInfo.symbol()));

        var startTime = symbolConfig.timeFrame().findStartFrame(lastStatement.timestamp());
        return Period.of(startTime, lastStatement.timestamp());
    }

    private void unlockSymbol(String symbol) {
        var unlockSymbol= symbolLockRepository.unlockSymbol(symbol);
        if (!unlockSymbol) {
            throw new FailedToLockSymbol("Failed to unlock symbol %s.".formatted(symbol));
        }
    }

}
