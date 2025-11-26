package com.smartshop.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import com.smartshop.DTO.Request.PaymentCreateRequest;
import com.smartshop.DTO.Response.ApiResponse;
import com.smartshop.DTO.Response.PaymentResponse;
import com.smartshop.Service.PaymentService;
import com.smartshop.Util.AuthHelper;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final AuthHelper authHelper;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> create(
            @Valid @RequestBody PaymentCreateRequest request,
            HttpSession session) {
        authHelper.requireAdmin(session);
        PaymentResponse payment = paymentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment registered successfully", payment));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> findById(
            @PathVariable Long id,
            HttpSession session) {
        authHelper.requireAdmin(session);
        PaymentResponse payment = paymentService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(payment));
    }

    @PutMapping("/{id}/cash")
    public ResponseEntity<ApiResponse<PaymentResponse>> markAsCashed(
            @PathVariable Long id,
            HttpSession session) {
        authHelper.requireAdmin(session);
        PaymentResponse payment = paymentService.markAsCashed(id);
        return ResponseEntity.ok(ApiResponse.success("Payment marked as cashed", payment));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<PaymentResponse>> markAsRejected(
            @PathVariable Long id,
            HttpSession session) {
        authHelper.requireAdmin(session);
        PaymentResponse payment = paymentService.markAsRejected(id);
        return ResponseEntity.ok(ApiResponse.success("Payment marked as rejected", payment));
    }
}
