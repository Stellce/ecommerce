package com.example.backend.order.dto.request;

import com.example.backend.order.item.dto.request.CreateOrderItemDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOrderRequest(
        @NotEmpty
        @JsonProperty("items")
        List<CreateOrderItemDto> orderItemDtos
) {}
