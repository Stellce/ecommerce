package com.example.backend.order.dto.request;

import com.example.backend.order.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record PatchOrderStatusRequest(
        @NotNull
        OrderStatus status
) {}
