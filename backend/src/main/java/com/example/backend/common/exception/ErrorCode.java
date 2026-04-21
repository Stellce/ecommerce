package com.example.backend.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    PRODUCT_IN_USE(HttpStatus.CONFLICT, "Product is referenced by existing orders"),
    DUPLICATE_ITEMS(HttpStatus.CONFLICT, "Duplicate items found"),
    ORDER_CANNOT_BE_CANCELLED(HttpStatus.CONFLICT, "Order cannot be cancelled" ),
    INSUFFICIENT_QUANTITY(HttpStatus.CONFLICT, "Insufficient quantity of the item"),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "Order not found"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid credentials"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "User already exists"),
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "Role not found"),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "Product not found"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad request"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Forbidden"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Not found"),
    CONFLICT(HttpStatus.CONFLICT, "Conflict"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");

    private final HttpStatus status;
    private final String message;
}
