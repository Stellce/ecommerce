package com.example.backend.order.item.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record OrderItemRequest(
        @NotNull
        UUID productId,
        @Positive
        int quantity
) {}
