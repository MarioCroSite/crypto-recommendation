package com.mario.cryptorecommendation.application.exception;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.springframework.http.HttpStatus;

import java.util.Objects;

@Builder
@Jacksonized
public record ApiError(
        String code,
        String status,
        String message
) {

    public ApiError {
        Objects.requireNonNull(code);
        Objects.requireNonNull(status);
        Objects.requireNonNull(message);
    }

    public static ApiError toApiError(
            String code,
            HttpStatus status,
            String message
    ) {
        return ApiError.builder()
                .code(code)
                .status(status.getReasonPhrase())
                .message(message)
                .build();
    }
}
