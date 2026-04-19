package com.example.backend.order;

import com.example.backend.common.dto.PageResponse;
import com.example.backend.order.dto.request.CreateOrderRequest;
import com.example.backend.order.dto.request.PatchOrderStatusRequest;
import com.example.backend.order.dto.response.OrderResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<PageResponse<OrderResponse>> getOrders(Pageable pageable) {
        PageResponse<OrderResponse> response = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') || @authService.canAccessOrder(#id)")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest orderRequest) {
        OrderResponse response = orderService.create(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/status/{id}")
    public ResponseEntity<OrderResponse> patchStatus(@PathVariable UUID id, @Valid @RequestBody PatchOrderStatusRequest patchOrderStatusRequest) {
        OrderResponse response = orderService.patchStatus(id, patchOrderStatusRequest);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('ADMIN') || @authService.canAccessOrder(#id)")
    public ResponseEntity<OrderResponse> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.cancel(id));
    }
}
