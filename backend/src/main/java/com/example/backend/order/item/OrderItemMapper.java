package com.example.backend.order.item;

import com.example.backend.order.Order;
import com.example.backend.order.item.dto.response.OrderItemResponse;
import com.example.backend.product.Product;
import com.example.backend.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class OrderItemMapper {
    private final ProductMapper productMapper;

    public OrderItem toEntity(Order order, Product product, int quantity, BigDecimal priceAtPurchase) {
        return new OrderItem(order, product, quantity, priceAtPurchase);
    }

    public OrderItemResponse toResponse(OrderItem orderItem) {

        return new OrderItemResponse(
                orderItem.getId(),
                productMapper.toResponse(orderItem.getProduct()),
                orderItem.getQuantity(),
                orderItem.getPriceAtPurchase()
        );
    }
}
