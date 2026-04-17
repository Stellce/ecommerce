package com.example.backend.order;

import com.example.backend.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "orders")
@DynamicInsert
@NoArgsConstructor
public class Order {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal totalPrice;

    @CreationTimestamp
    @Column(nullable = false)
    private OffsetDateTime createdAt;

    public Order(User user, OrderStatus status, BigDecimal totalPrice) {
        this.user = user;
        this.status = status;
        this.totalPrice = totalPrice;
    }
}
