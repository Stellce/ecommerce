package com.example.backend.order.dto.response;

import com.example.backend.order.OrderStatus;
import com.example.backend.order.item.dto.response.OrderItemResponse;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        List<OrderItemResponse> items,
        OrderStatus status,
        BigDecimal totalPrice,
        OffsetDateTime createdAt
) {}
