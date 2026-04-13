package com.example.backend.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends BaseException {
    public InvalidTokenException() {
        super("INVALID_TOKEN", "Invalid token", HttpStatus.UNAUTHORIZED);
    }
}
