package com.example.backend.order.item.dto.request;

import java.util.UUID;

public record CreateOrderItemDto(
        UUID productId,
        int quantity
) {}
