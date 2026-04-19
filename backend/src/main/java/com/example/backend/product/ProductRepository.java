package com.example.backend.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    @Modifying
    @Query("""
        update Product p
        set p.stock = p.stock - :quantity
        where p.id = :productId and p.stock >= :quantity
    """)
    int decreaseStockIfEnough(UUID productId, int quantity);
}
