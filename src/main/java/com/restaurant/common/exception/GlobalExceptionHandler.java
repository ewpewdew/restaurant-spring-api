package com.restaurant.common.exception;

import com.restaurant.common.dto.ApiErrorResponse;
import com.restaurant.common.enums.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException e) {
        HttpStatus status = switch (e.getErrorCode()) {
            case VALIDATION_ERROR,
                 INVALID_JSON,
                 USER_NOT_FOUND,
                 EMAIL_ALREADY_EXISTS,
                 PHONE_ALREADY_EXISTS -> HttpStatus.BAD_REQUEST;
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

    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception e) {
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
