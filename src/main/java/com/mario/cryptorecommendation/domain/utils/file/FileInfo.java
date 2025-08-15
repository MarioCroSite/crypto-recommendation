package com.mario.cryptorecommendation.domain.utils.file;

public record FileInfo(
        String symbol,
        String filePath,
        ExtensionType extensionType
) {
    public static FileInfo of(String symbol, String filePath, ExtensionType extensionType) {
        return new FileInfo(symbol, filePath, extensionType);
    }
}
