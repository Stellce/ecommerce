package com.example.backend.order.item.dto.response;

import com.example.backend.product.dto.response.ProductResponse;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        ProductResponse product,
        Integer quantity,
        BigDecimal priceAtPurchase
) {}
