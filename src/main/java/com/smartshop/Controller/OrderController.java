package com.smartshop.Controller;

import com.smartshop.DTO.Request.OrderCreateRequest;
import com.smartshop.DTO.Response.ApiResponse;
import com.smartshop.DTO.Response.OrderResponse;
import com.smartshop.DTO.Response.PaymentResponse;
import com.smartshop.Enums.OrderStatus;
import com.smartshop.Service.OrderService;
import com.smartshop.Service.PaymentService;
import com.smartshop.Util.AuthHelper;
import jakarta.servlet.http.HttpSession;
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
    private final PaymentService paymentService;
    private final AuthHelper authHelper;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> create(
            @Valid @RequestBody OrderCreateRequest request,
            HttpSession session) {
        authHelper.requireAdmin(session);
        OrderResponse order = orderService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(order, "Order created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> findAll(HttpSession session) {
        authHelper.requireAdmin(session);
        List<OrderResponse> orders = orderService.findAll();
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> findById(
            @PathVariable Long id,
            HttpSession session) {
        authHelper.requireAdmin(session);
        OrderResponse order = orderService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> findByStatus(
            @PathVariable OrderStatus status,
            HttpSession session) {
        authHelper.requireAdmin(session);
        List<OrderResponse> orders = orderService.findByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/{id}/payments")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getOrderPayments(
            @PathVariable Long id,
            HttpSession session) {
        authHelper.requireAdmin(session);
        List<PaymentResponse> payments = paymentService.findByOrderId(id);
        return ResponseEntity.ok(ApiResponse.success(payments));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<OrderResponse>> confirm(
            @PathVariable Long id,
            HttpSession session) {
        authHelper.requireAdmin(session);
        OrderResponse order = orderService.confirm(id);
        return ResponseEntity.ok(ApiResponse.success(order, "Order confirmed successfully"));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancel(
            @PathVariable Long id,
            HttpSession session) {
        authHelper.requireAdmin(session);
        OrderResponse order = orderService.cancel(id);
        return ResponseEntity.ok(ApiResponse.success(order, "Order canceled successfully"));
    }
}
