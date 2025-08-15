package com.mario.cryptorecommendation.domain.utils.file;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.util.Collections.emptyList;

@Slf4j
public class FileInfoUtil {

    /**
     * Retrieves a list of FileInfo objects from the specified directory.
     *
     * @param directoryLocation the directory to scan
     * @return list of FileInfo, or empty list if error occurs
     */
    public static List<FileInfo> getFilesFromDirectoryLocation(String directoryLocation) {
        if (directoryLocation == null || directoryLocation.isBlank()) {
            log.warn("Directory location is null or blank");
            return emptyList();
        }

        try (var paths = Files.walk(Paths.get(directoryLocation))) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(path -> {
                        final String fileName = path.getFileName().toString();
                        if (!fileName.contains("_")) {
                            log.warn("Skipping file without underscore: {}", fileName);
                            return null;
                        }
                        final String currencyName = fileName.split("_")[0];
                        return FileInfo.of(currencyName, path.toString(), getFileExtension(path));
                    })
                    .filter(java.util.Objects::nonNull)
                    .toList();
        } catch (IOException e) {
            log.error("Error fetching file paths from directory location", e);
            return emptyList();
        }
    }

    private static ExtensionType getFileExtension(Path path) {
        if (path == null) return null;
        String name = path.getFileName().toString();
        int lastDot = name.lastIndexOf('.');
        if (lastDot <= 0 || lastDot == name.length() - 1) return null;
        String extension = name.substring(lastDot + 1);
        try {
            return ExtensionType.fromValue(extension);
        } catch (IllegalArgumentException e) {
            log.warn("Unknown file extension: {}", extension);
            return null;
        }
    }
}
