package com.mario.cryptorecommendation.domain.reader;

import com.mario.cryptorecommendation.domain.ingestion.reader.FileReaderTxt;
import com.mario.cryptorecommendation.domain.utils.file.FileInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.mario.cryptorecommendation.domain.utils.file.ExtensionType.TXT;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class FileReaderTxtTest {

    private FileReaderTxt txtReader;

    @BeforeEach
    void setUp() {
        txtReader = new FileReaderTxt();
    }

    @Test
    void shouldThrowUnsupportedOperationException() {
        // Given
        var fileInfo = new FileInfo("BTC", "test.txt", TXT);

        // When & Then
        assertThatThrownBy(() -> txtReader.readStatements(fileInfo))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Not supported yet.");
    }
}
