package com.example.backend.order;

import com.example.backend.common.dto.PageResponse;
import com.example.backend.common.exception.AppException;
import com.example.backend.common.exception.ErrorCode;
import com.example.backend.order.dto.request.CreateOrderRequest;
import com.example.backend.order.dto.response.OrderResponse;
import com.example.backend.order.item.OrderItem;
import com.example.backend.order.item.OrderItemMapper;
import com.example.backend.order.item.OrderItemRepository;
import com.example.backend.order.item.dto.response.OrderItemResponse;
import com.example.backend.order.mapper.OrderMapper;
import com.example.backend.product.Product;
import com.example.backend.product.ProductRepository;
import com.example.backend.security.CustomUserPrincipal;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponse create(CreateOrderRequest orderRequest) {
        List<OrderItem> orderItems = orderRequest.orderItemDtos().stream().map(orderItemDto -> {
            Product product = productRepository.findById(orderItemDto.productId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            if (product.getStock() < orderItemDto.quantity()) {
                throw new AppException(ErrorCode.INSUFFICIENT_QUANTITY);
            }

            product.setStock(product.getStock() - orderItemDto.quantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(orderItemDto.quantity());
            orderItem.setPriceAtPurchase(product.getPrice());

            return orderItem;
        }).toList();

        BigDecimal totalPrice = orderItems.stream()
                .map(orderItem -> orderItem.getPriceAtPurchase().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        if (principal == null || principal.getId() == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Order order = new Order(
                user,
                OrderStatus.CREATED,
                totalPrice
        );
        Order createdOrder = orderRepository.save(order);

        List<OrderItem> createdOrderItems = orderItems.stream().map(orderItem -> {
            orderItem.setOrder(createdOrder);
            return orderItemRepository.save(orderItem);
        }).toList();

        List<OrderItemResponse> orderItemsResponse = createdOrderItems.stream().map(orderItemMapper::toResponse).toList();

        return new OrderResponse(
                createdOrder.getId(),
                orderItemsResponse,
                createdOrder.getStatus(),
                createdOrder.getTotalPrice(),
                createdOrder.getCreatedAt()
        );
    }

    public OrderResponse findById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        return orderMapper.toResponse(order);
    }

    public PageResponse<OrderResponse> getAllOrders(Pageable pageable) {
        Page<OrderResponse> orders = orderRepository.findAll(pageable)
                .map(orderMapper::toResponse);
        return PageResponse.of(
                orders
        );
    }
}
