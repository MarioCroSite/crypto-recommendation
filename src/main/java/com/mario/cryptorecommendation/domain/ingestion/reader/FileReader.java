package com.mario.cryptorecommendation.domain.ingestion.reader;

import com.mario.cryptorecommendation.domain.utils.file.FileInfo;

import java.util.List;

public interface FileReader {

    List<Statement> readStatements(FileInfo fileInfo);
}
