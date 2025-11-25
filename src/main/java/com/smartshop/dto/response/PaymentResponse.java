package com.smartshop.dto.response;

import com.smartshop.enums.PaymentMethod;
import com.smartshop.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private Integer paymentNumber;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private LocalDateTime paymentDate;
    private LocalDateTime encashmentDate;
    private PaymentStatus status;
    
    // Method-specific fields
    private String receiptNumber;
    private String checkNumber;
    private String bank;
    private LocalDateTime dueDate;
    private String transferReference;
}
