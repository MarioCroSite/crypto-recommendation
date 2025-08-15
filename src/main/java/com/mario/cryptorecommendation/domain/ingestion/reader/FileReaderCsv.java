package com.mario.cryptorecommendation.domain.ingestion.reader;

import com.mario.cryptorecommendation.domain.utils.file.FileInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component("csvReader")
public class FileReaderCsv implements FileReader {
    private static final String SEPARATOR = ",";
    private static final int EXPECTED_COLUMNS = 3;
    private static final int TIMESTAMP_INDEX = 0;
    private static final int CURRENCY_INDEX = 1;
    private static final int RATE_INDEX = 2;


    @Override
    public List<Statement> readStatements(FileInfo fileInfo) {
        if (fileInfo == null || fileInfo.filePath() == null) {
            log.error("Invalid file info provided");
            throw new FileReadFailedException("Invalid file info provided", null);
        }

        try (var reader = Files.newBufferedReader(Path.of(fileInfo.filePath()))) {
            return reader.lines()
                    .skip(1) // Skip header
                    .filter(this::isNotBlank)
                    .map(this::parseLine)
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(Statement::timestamp).reversed())
                    .toList();

        } catch (NoSuchFileException e) {
            log.error("File not found: {}", fileInfo.filePath());
            throw new FileReadFailedException("File not found: " + fileInfo.filePath(), e);
        } catch (Exception e) {
            log.error("Error reading file: {}", fileInfo.filePath(), e);
            throw new FileReadFailedException("Error reading: " + fileInfo.filePath(), e);
        }
    }

    private boolean isNotBlank(String line) {
        return line != null && !line.trim().isEmpty();
    }

    private Statement parseLine(String line) {
        try {
            String[] parts = line.split(SEPARATOR);
            if (parts.length != EXPECTED_COLUMNS) {
                log.warn("Invalid line format: {}", line);
                return null;
            }

            return new Statement(
                    Instant.ofEpochMilli(Long.parseLong(parts[TIMESTAMP_INDEX].trim())),
                    parts[CURRENCY_INDEX].trim(),
                    new BigDecimal(parts[RATE_INDEX].trim())
            );
        } catch (NumberFormatException | DateTimeException e) {
            log.warn("Error parsing line: {}", line, e);
            return null;
        }
    }
}
