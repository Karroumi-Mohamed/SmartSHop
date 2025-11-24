package com.smartshop.Service;

import java.util.List;

import com.smartshop.DTO.Request.OrderCreateRequest;
import com.smartshop.DTO.Response.OrderResponse;
import com.smartshop.Enums.OrderStatus;

public interface OrderService {
    OrderResponse create(OrderCreateRequest request);

    OrderResponse findById(Long id);

    List<OrderResponse> findAll();

    List<OrderResponse> findByClientId(Long clientId);

    List<OrderResponse> findByStatus(OrderStatus status);

    OrderResponse confirm(Long id);

    OrderResponse cancel(Long id);
}
