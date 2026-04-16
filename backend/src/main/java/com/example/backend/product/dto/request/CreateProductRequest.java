package com.example.backend.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateProductRequest (
        @NotBlank
        String name,

        @NotBlank
        String description,

        @NotNull
        @PositiveOrZero
        BigDecimal price,

        @NotNull
        @PositiveOrZero
        Integer stock
) {}
