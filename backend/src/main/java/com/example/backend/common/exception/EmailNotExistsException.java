package com.example.backend.common.exception;

import org.springframework.http.HttpStatus;

public class EmailNotExistsException extends BaseException {
    public EmailNotExistsException() {
        super("EMAIL_NOT_EXISTS", "Email Not Exists", HttpStatus.NOT_FOUND);
    }
}
