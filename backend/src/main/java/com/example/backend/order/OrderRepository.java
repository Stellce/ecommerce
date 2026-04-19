package com.example.backend.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Boolean existsByIdAndUserId(UUID id, UUID userId);
    Page<Order> findAllByUserId(UUID userId, Pageable pageable);
}
