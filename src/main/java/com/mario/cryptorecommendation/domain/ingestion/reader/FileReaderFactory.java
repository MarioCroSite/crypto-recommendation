package com.mario.cryptorecommendation.domain.ingestion.reader;

import com.mario.cryptorecommendation.domain.utils.file.ExtensionType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Optional;

@Component
public class FileReaderFactory {

    private final Map<ExtensionType, FileReader> fileReaders;

    public FileReaderFactory(@Qualifier("csvReader") FileReader csvReader,
                             @Qualifier("txtReader") FileReader txtReader) {
        this.fileReaders = Map.of(
                ExtensionType.CSV, csvReader,
                ExtensionType.TXT, txtReader
        );
    }

    public FileReader getFileReader(ExtensionType extensionType) {
        return Optional.ofNullable(fileReaders.get(extensionType))
                .orElseThrow(() -> new IllegalArgumentException("Unsupported file type: " + extensionType));
    }
}
