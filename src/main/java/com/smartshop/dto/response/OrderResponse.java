package com.smartshop.dto.response;

import com.smartshop.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Long clientId;
    private String clientName;
    private LocalDateTime orderDate;
    private BigDecimal subTotal;
    private BigDecimal discountAmount;
    private BigDecimal vatAmount;
    private BigDecimal totalInclTax;
    private String promoCode;
    private OrderStatus status;
    private BigDecimal remainingAmount;
    private List<OrderItemResponse> items;
}
