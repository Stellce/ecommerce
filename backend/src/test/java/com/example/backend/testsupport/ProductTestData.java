package com.example.backend.testsupport;

import com.example.backend.product.dto.request.CreateProductRequest;

import java.math.BigDecimal;

public final class ProductTestData {

    private ProductTestData() {
    }

    public static CreateProductRequest validCreateProductRequest() {
        return new CreateProductRequest(
                "Test product",
                "Test description",
                new BigDecimal("10.99"),
                5
        );
    }
}
