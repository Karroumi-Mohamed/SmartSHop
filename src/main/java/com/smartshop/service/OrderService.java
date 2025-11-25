package com.smartshop.service;

import com.smartshop.dto.request.OrderCreateRequest;
import com.smartshop.dto.response.OrderResponse;
import com.smartshop.enums.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderCreateRequest request);
    OrderResponse getOrderById(Long id);
    List<OrderResponse> getOrdersByClient(Long clientId);
    OrderResponse confirmOrder(Long orderId);
    OrderResponse cancelOrder(Long orderId);
    OrderResponse updateOrderStatus(Long orderId, OrderStatus status);
}
