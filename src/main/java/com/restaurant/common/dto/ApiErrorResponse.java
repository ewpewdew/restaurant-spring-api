package com.restaurant.common.dto;

import com.restaurant.common.enums.ErrorCode;

import java.time.LocalDateTime;

public record ApiErrorResponse(
        int status,
        ErrorCode errorCode,
        String message,
        LocalDateTime errorTime

) {
}
