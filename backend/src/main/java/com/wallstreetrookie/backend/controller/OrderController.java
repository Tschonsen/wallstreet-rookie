package com.wallstreetrookie.backend.controller;

import com.wallstreetrookie.backend.dto.request.CreateOrderRequest;
import com.wallstreetrookie.backend.dto.response.OrderResponse;
import com.wallstreetrookie.backend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            Authentication auth,
            @Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(auth.getPrincipal().toString(), request));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOpenOrders(Authentication auth) {
        return ResponseEntity.ok(orderService.getOpenOrders(auth.getPrincipal().toString()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(Authentication auth, @PathVariable String id) {
        orderService.cancelOrder(auth.getPrincipal().toString(), id);
        return ResponseEntity.noContent().build();
    }
}
