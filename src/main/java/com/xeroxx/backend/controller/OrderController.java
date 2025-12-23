package com.xeroxx.backend.controller;

import com.xeroxx.backend.dto.OrderRequest;
import com.xeroxx.backend.dto.OrderStatusUpdateRequest;
import com.xeroxx.backend.dto.OrderSummaryResponse;
import com.xeroxx.backend.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderSummaryResponse> create(
        @RequestBody OrderRequest request,
        Authentication authentication) {

    String username = authentication.getName();
    return ResponseEntity.ok(
            orderService.createOrder(request, username)
    );
}

    @GetMapping
    public ResponseEntity<List<OrderSummaryResponse>> list(Authentication authentication) {

    String username = authentication.getName();
    return ResponseEntity.ok(
            orderService.list(username)
    );
}

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderSummaryResponse> getById(
        @PathVariable Long orderId,
        Authentication authentication) {

    String username = authentication.getName();
    return ResponseEntity.ok(
            orderService.getById(orderId, username)
    );
}

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderSummaryResponse> updateStatus(
            @PathVariable Long orderId,
            @RequestBody @Valid OrderStatusUpdateRequest request) {

        return ResponseEntity.ok(
                orderService.updateStatus(orderId, request.getStatus())
        );
    }
}
