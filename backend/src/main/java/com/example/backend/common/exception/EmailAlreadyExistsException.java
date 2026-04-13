package com.example.backend.common.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends BaseException {
    public EmailAlreadyExistsException() {
        super("EMAIL_ALREADY_EXISTS", "Email Already Exists", HttpStatus.CONFLICT);
    }
}
