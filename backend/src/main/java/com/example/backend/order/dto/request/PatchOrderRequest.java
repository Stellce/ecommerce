package com.example.backend.order.dto.request;

import com.example.backend.order.item.dto.request.CreateOrderItemDto;

import java.util.List;

public record PatchOrderRequest(
        List<CreateOrderItemDto> orderItemDtos
) {}
