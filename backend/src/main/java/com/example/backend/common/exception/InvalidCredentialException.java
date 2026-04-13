package com.example.backend.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialException extends BaseException{
    public InvalidCredentialException() {
        super("INVALID_CREDENTIAL", "Invalid Credential", HttpStatus.UNAUTHORIZED);
    }
}
