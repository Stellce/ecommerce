package com.example.backend.auth.dto.response;

public record ErrorResponse (
        String timestamp,
        int status,
        String error,
        String message,
        String path
) {}
