package com.mario.cryptorecommendation.domain.utils.file;

import java.util.Arrays;

public enum ExtensionType {
    CSV("csv"),
    TXT("txt");

    private final String value;

    ExtensionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ExtensionType fromValue(String value) {
        return Arrays.stream(values())
                .filter(ext -> ext.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown file extension: " + value));
    }
}
