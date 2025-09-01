package com.mario.cryptorecommendation.domain.ingestion.reader;

import com.mario.cryptorecommendation.domain.utils.file.FileInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static com.mario.cryptorecommendation.domain.utils.file.ExtensionType.CSV;
import static java.time.Instant.ofEpochMilli;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class FileReaderCsvTest {

    private FileReaderCsv csvReader;

    @BeforeEach
    void setUp() {
        csvReader = new FileReaderCsv();
    }

    @Test
    void shouldReadExistingBtcValuesFile() {
        // Given
        var symbol = "BTC";
        var fileName = "statements/BTC_values.csv";
        var expectedData = testBtcData();

        // When
        var statements = readCsvFile(symbol, fileName);

        // Then
        assertThat(statements).hasSize(expectedData.size());
        for (int i = 0; i < 10; i++) {
            var actual = statements.get(i);
            var expected = expectedData.get(i);

            assertThat(actual.timestamp()).isEqualTo(expected.timestamp());
            assertThat(actual.symbol()).isEqualTo(expected.symbol());
            assertThat(actual.price()).isEqualTo(expected.price());
        }
    }

    @ParameterizedTest
    @MethodSource("provideInvalidFileInfoScenarios")
    void shouldThrowExceptionForInvalidFileInfo(FileInfo fileInfo, String expectedMessage) {
        // When & Then
        assertThatThrownBy(() -> csvReader.readStatements(fileInfo))
                .isInstanceOf(FileReadFailedException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    void shouldHandleEmptyFile() {
        // Given
        var symbol = "BTC";
        var fileName = "statements/empty.csv";

        // When
        var statements = readCsvFile(symbol, fileName);

        // Then
        assertThat(statements).isEmpty();
    }

    private static Stream<Arguments> provideInvalidFileInfoScenarios() {
        return Stream.of(
                Arguments.of(null, "Invalid file info provided"),
                Arguments.of(new FileInfo("BTC", null, CSV), "Invalid file info provided"),
                Arguments.of(new FileInfo(null, "path/to/file.csv", CSV), "File not found: path/to/file.csv")
        );
    }

    private List<TestData> testBtcData() {
        return Stream.of(
                new TestData(ofEpochMilli(1641009600000L), "BTC", new BigDecimal("46813.21")),
                new TestData(ofEpochMilli(1641020400000L), "BTC", new BigDecimal("46979.61")),
                new TestData(ofEpochMilli(1641031200000L), "BTC", new BigDecimal("47143.98")),
                new TestData(ofEpochMilli(1641034800000L), "BTC", new BigDecimal("46871.09")),
                new TestData(ofEpochMilli(1641045600000L), "BTC", new BigDecimal("47023.24")),
                new TestData(ofEpochMilli(1641081600000L), "BTC", new BigDecimal("47722.66")),
                new TestData(ofEpochMilli(1641160800000L), "BTC", new BigDecimal("47017.98")),
                new TestData(ofEpochMilli(1641175200000L), "BTC", new BigDecimal("47116.22")),
                new TestData(ofEpochMilli(1641243600000L), "BTC", new BigDecimal("45922.01")),
                new TestData(ofEpochMilli(1641308400000L), "BTC", new BigDecimal("47336.98"))
        )
                .sorted(Comparator.comparing(TestData::timestamp).reversed())
                .toList();
    }

    private List<Statement> readCsvFile(String symbol, String fileName) {
        var filePath = getResourcePath(fileName);
        var fileInfo = new FileInfo(symbol, filePath, CSV);
        return csvReader.readStatements(fileInfo);
    }

    private String getResourcePath(String resourceName) {
        try {
            return Paths.get(requireNonNull(getClass().getResource("/" + resourceName)).toURI()).toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to get resource path: " + resourceName, e);
        }
    }

    private record TestData(Instant timestamp, String symbol, BigDecimal price) {
    }
}

