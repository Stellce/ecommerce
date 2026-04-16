package com.example.backend.product.dto.request;

import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record PatchProductRequest(
    String name,
    String description,

    @PositiveOrZero
    BigDecimal price,

    @PositiveOrZero
    Integer stock
) {}
