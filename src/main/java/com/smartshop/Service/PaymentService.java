package com.smartshop.Service;

import java.util.List;

import com.smartshop.DTO.Request.PaymentCreateRequest;
import com.smartshop.DTO.Response.PaymentResponse;

public interface PaymentService {
    PaymentResponse create(PaymentCreateRequest request);

    PaymentResponse findById(Long id);

    List<PaymentResponse> findAll();

    List<PaymentResponse> findByOrderId(Long orderId);

    PaymentResponse markAsCashed(Long id);

    PaymentResponse markAsRejected(Long id);
}
