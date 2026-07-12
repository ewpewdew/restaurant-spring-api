package com.restaurant.common.dto;

import com.restaurant.common.enums.ErrorCode;

public record ApiErrorResponse(
        int status,
        ErrorCode errorCode,
        String message
) {
}
