package com.restaurant.common.exception;

import com.restaurant.common.dto.ApiErrorResponse;
import com.restaurant.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException e) {
        HttpStatus status = switch (e.getErrorCode()) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case VALIDATION_ERROR, INVALID_JSON, EMAIL_ALREADY_EXISTS, PHONE_ALREADY_EXISTS -> HttpStatus.BAD_REQUEST;
            case INTERNAL_SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
        return ResponseEntity.status(status).body(
                new ApiErrorResponse(
                        status.value(),
                        e.getErrorCode(),
                        e.getMessage(),
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        String errors = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(
                new ApiErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        ErrorCode.VALIDATION_ERROR,
                        errors,
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(exception = {
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    public ResponseEntity<ApiErrorResponse> handleBadRequest(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(
                new ApiErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        ErrorCode.INVALID_JSON,
                        "Некорректный JSON",
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidJson(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(
                new ApiErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        ErrorCode.INVALID_JSON,
                        "Некорректный JSON",
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception e) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ApiErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        "Внутренняя ошибка сервера",
                        LocalDateTime.now()
                )
        );
    }
}
