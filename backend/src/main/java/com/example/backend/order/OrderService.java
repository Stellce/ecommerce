package com.example.backend.order;

import com.example.backend.common.dto.PageResponse;
import com.example.backend.common.exception.AppException;
import com.example.backend.common.exception.ErrorCode;
import com.example.backend.order.dto.request.CreateOrderRequest;
import com.example.backend.order.dto.request.PatchOrderStatusRequest;
import com.example.backend.order.dto.response.OrderResponse;
import com.example.backend.order.item.OrderItem;
import com.example.backend.order.item.OrderItemMapper;
import com.example.backend.order.item.OrderItemRepository;
import com.example.backend.order.item.dto.request.OrderItemRequest;
import com.example.backend.order.item.dto.response.OrderItemResponse;
import com.example.backend.order.mapper.OrderMapper;
import com.example.backend.product.Product;
import com.example.backend.product.ProductRepository;
import com.example.backend.security.CurrentUserService;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private static final EnumSet<OrderStatus> CANCELLABLE_STATUSES = EnumSet.of(OrderStatus.CREATED, OrderStatus.PAID);
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final CurrentUserService currentUserService;

    @Transactional
    public OrderResponse create(CreateOrderRequest orderRequest) {
        Set<UUID> productIds = orderRequest.orderItemRequests().stream()
                .map(OrderItemRequest::productId)
                .collect(Collectors.toSet());

        if (productIds.size() != orderRequest.orderItemRequests().size()) {
            throw new AppException(ErrorCode.DUPLICATE_ITEMS);
        }

        Map<UUID, Product> products = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        List<OrderItem> orderItems = orderRequest.orderItemRequests().stream().map(orderItemDto -> {
            Product product = products.get(orderItemDto.productId());
            if (product == null) throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);

            int decrease = productRepository.decreaseStockIfEnough(orderItemDto.productId(), orderItemDto.quantity());
            if (decrease == 0) throw new AppException(ErrorCode.INSUFFICIENT_QUANTITY);

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(orderItemDto.quantity());
            orderItem.setPriceAtPurchase(product.getPrice());

            return orderItem;
        }).toList();

        BigDecimal totalPrice = orderItems.stream()
                .map(orderItem -> orderItem.getPriceAtPurchase()
                        .multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        User user = userRepository.findById(currentUserService.getCurrentUserId())
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

        List<OrderItemResponse> orderItemsResponse = createdOrderItems.stream()
                .map(orderItemMapper::toResponse).toList();

        return new OrderResponse(
                createdOrder.getId(),
                orderItemsResponse,
                createdOrder.getStatus(),
                createdOrder.getTotalPrice(),
                createdOrder.getCreatedAt()
        );
    }

    public OrderResponse getOrderById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        return orderMapper.toResponse(order, order.getOrderItems());
    }

    public PageResponse<OrderResponse> getAllOrders(Pageable pageable) {
        Page<Order> orderPage = currentUserService.isAdmin()
                ? orderRepository.findAll(pageable)
                : orderRepository.findAllByUserId(currentUserService.getCurrentUserId(), pageable);
        List<UUID> orderIds = orderPage.getContent().stream()
                .map(Order::getId)
                .toList();

        Map<UUID, List<OrderItem>> itemsByOrderId = orderItemRepository.findAllByOrderIdIn(orderIds).stream()
                .collect(Collectors.groupingBy(item -> item.getOrder().getId()));

        Page<OrderResponse> responsePage = orderPage.map(order ->
                orderMapper.toResponse(order, itemsByOrderId.getOrDefault(order.getId(), List.of())));

        return PageResponse.of(responsePage);
    }

    public OrderResponse patchStatus(UUID orderId, PatchOrderStatusRequest patchOrderStatusRequest) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        order.setStatus(patchOrderStatusRequest.status());
        orderRepository.save(order);
        return orderMapper.toResponse(order, order.getOrderItems());
    }

    @Transactional
    public OrderResponse cancel(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (!CANCELLABLE_STATUSES.contains(order.getStatus())) {
            throw new AppException(ErrorCode.ORDER_CANNOT_BE_CANCELLED);
        }

        order.setStatus(OrderStatus.CANCELLED);

        List<OrderItem> orderItems = order.getOrderItems();

        for (OrderItem item : orderItems) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
        }

        return orderMapper.toResponse(order, orderItems);
    }
}
