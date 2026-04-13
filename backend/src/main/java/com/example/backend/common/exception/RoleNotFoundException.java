package com.example.backend.common.exception;

import org.springframework.http.HttpStatus;

public class RoleNotFoundException extends BaseException {
    public RoleNotFoundException() {
        super("ROLE_NOT_FOUND", "Role Not Found", HttpStatus.NOT_FOUND);
    }
}
