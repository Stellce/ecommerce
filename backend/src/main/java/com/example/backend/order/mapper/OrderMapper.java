package com.example.backend.order.mapper;

import com.example.backend.order.Order;
import com.example.backend.order.dto.response.OrderResponse;
import com.example.backend.order.item.OrderItem;
import com.example.backend.order.item.OrderItemMapper;
import com.example.backend.order.item.dto.response.OrderItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class OrderMapper {
    private final OrderItemMapper orderItemMapper;

    public OrderResponse toResponse(Order order, List<OrderItem> orderItems) {
        List<OrderItemResponse> orderItemResponses = orderItems.stream().map(orderItemMapper::toResponse).toList();
        return new OrderResponse(
                order.getId(),
                orderItemResponses,
                order.getStatus(),
                order.getTotalPrice(),
                order.getCreatedAt()
        );
    }
}
