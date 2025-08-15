package com.mario.cryptorecommendation.domain.ingestion.reader;

import com.mario.cryptorecommendation.domain.utils.file.FileInfo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("txtReader")
public class FileReaderTxt implements FileReader {
    @Override
    public List<Statement> readStatements(FileInfo fileInfo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
