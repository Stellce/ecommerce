package com.example.backend.common.dto;

import org.springframework.http.HttpStatus;

import java.time.Instant;

public record ErrorResponse (
        String timestamp,
        int status,
        String error,
        String message,
        String path
) {
    public static ErrorResponse of(
            HttpStatus status,
            String error,
            String message,
            String path
    ) {
        return new ErrorResponse(
                Instant.now().toString(),
                status.value(),
                error,
                message,
                path
        );
    }
}
