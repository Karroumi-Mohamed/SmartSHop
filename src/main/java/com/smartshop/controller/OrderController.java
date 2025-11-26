package com.smartshop.controller;

import com.smartshop.dto.request.OrderCreateRequest;
import com.smartshop.dto.response.OrderResponse;
import com.smartshop.enums.OrderStatus;
import com.smartshop.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByClient(@PathVariable Long clientId) {
        List<OrderResponse> orders = orderService.getOrdersByClient(clientId);
        return ResponseEntity.ok(orders);
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<OrderResponse> confirmOrder(@PathVariable Long id) {
        OrderResponse response = orderService.confirmOrder(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id) {
        OrderResponse response = orderService.cancelOrder(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        OrderResponse response = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(response);
    }
}
