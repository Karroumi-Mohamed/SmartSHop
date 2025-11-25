package com.smartshop.service;

import com.smartshop.dto.request.PaymentCreateRequest;
import com.smartshop.dto.response.PaymentResponse;

import java.util.List;

public interface PaymentService {
    PaymentResponse createPayment(PaymentCreateRequest request);
    PaymentResponse getPaymentById(Long id);
    List<PaymentResponse> getPaymentsByOrder(Long orderId);
    PaymentResponse updatePaymentStatus(Long paymentId, com.smartshop.enums.PaymentStatus status);
}
