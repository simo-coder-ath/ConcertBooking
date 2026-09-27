package com.example.concert_booking_api.core.response;

public record ApiResponse<T>(
        boolean success,
        String message,
        T data
) {
}
