package com.example.backend.order.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Boolean existsByProductId(UUID id);
    List<OrderItem> findAllByOrderIdIn(List<UUID> ids);
}
