package com.mario.cryptorecommendation.application.exception;

import com.mario.cryptorecommendation.domain.recommendation.SymbolNotSupportedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGlobalException(Exception ex) {
        log.error("Exception: An unexpected error occurred", ex);
        var error = ApiError.toApiError("UNEXPECTED_ERROR", INTERNAL_SERVER_ERROR, ex.getMessage());
        return ResponseEntity.internalServerError().body(error);
    }

    @ExceptionHandler(SymbolNotSupportedException.class)
    public ResponseEntity<String> handleSymbolNotSupportedException(SymbolNotSupportedException ex) {
        log.error("SymbolNotSupportedException: Symbol not supported", ex);
        return  ResponseEntity.badRequest().body("Symbol not supported");
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<String> handleDateTimeParseException(DateTimeParseException e) {
        return ResponseEntity.badRequest()
                .body("Invalid date format. Please use dd-MM-yyyy");
    }
}
