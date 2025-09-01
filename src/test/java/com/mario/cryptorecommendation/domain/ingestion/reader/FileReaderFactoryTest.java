package com.mario.cryptorecommendation.domain.ingestion.reader;

import com.mario.cryptorecommendation.domain.utils.file.ExtensionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class FileReaderFactoryTest {

    @Mock
    private FileReader csvReader;

    @Mock
    private FileReader txtReader;

    private FileReaderFactory factory;

    @BeforeEach
    void setUp() {
        factory = new FileReaderFactory(csvReader, txtReader);
    }

    @Test
    void shouldReturnCsvReaderForCsvExtension() {
        // When
        var result = factory.getFileReader(ExtensionType.CSV);

        // Then
        assertThat(result).isEqualTo(csvReader);
    }

    @Test
    void shouldReturnTxtReaderForTxtExtension() {
        // When
        var result = factory.getFileReader(ExtensionType.TXT);

        // Then
        assertThat(result).isEqualTo(txtReader);
    }

    @Test
    void shouldThrowExceptionForUnsupportedExtension() {
        // Given
        var extensionType = "UNKNOWN";

        // When & Then
        assertThatThrownBy(() -> factory.getFileReader(ExtensionType.valueOf(extensionType)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No enum constant");
    }

}
