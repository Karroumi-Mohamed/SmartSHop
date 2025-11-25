package com.smartshop.DTO.Response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.smartshop.Enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long id;

    private Long clientId;
    private String clientName;

    private LocalDateTime orderDate;
    private OrderStatus status;

    private List<OrderItemResponse> items;

    private BigDecimal subTotal;
    private String promoCode;
    private BigDecimal discountPercentage;
    private BigDecimal discountAmount;
    private BigDecimal amountAfterDiscount;
    private BigDecimal tvaRate;
    private BigDecimal tvaAmount;
    private BigDecimal totalTTC;
    private BigDecimal remainingAmount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
