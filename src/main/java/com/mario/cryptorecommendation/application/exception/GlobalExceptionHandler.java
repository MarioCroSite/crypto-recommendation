package com.mario.cryptorecommendation.application.exception;

import com.mario.cryptorecommendation.domain.recommendation.NoDataFoundException;
import com.mario.cryptorecommendation.domain.recommendation.SymbolNotSupportedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

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
    public ResponseEntity<ApiError> handleSymbolNotSupportedException(SymbolNotSupportedException ex) {
        log.info("SymbolNotSupportedException: Symbol not supported", ex);
        var error = ApiError.toApiError("SYMBOL_NOT_SUPPORTED", BAD_REQUEST, ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<ApiError> handleNoDataFoundException(NoDataFoundException ex) {
        log.info("NoDataFoundException: No data found for request", ex);
        var error = ApiError.toApiError("NO_DATA_FOUND", NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(NOT_FOUND).body(error);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ApiError> handleDateTimeParseException(DateTimeParseException e) {
        log.info("DateTimeParseException: Invalid date format", e);
        var error = ApiError.toApiError("INVALID_DATE_FORMAT", BAD_REQUEST, "Invalid date format. Please use dd-MM-yyyy");
        return ResponseEntity.badRequest().body(error);
    }
}
