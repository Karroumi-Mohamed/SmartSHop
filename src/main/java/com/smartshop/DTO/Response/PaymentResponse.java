package com.smartshop.DTO.Response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.smartshop.Enums.PaymentMethod;
import com.smartshop.Enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {

    private Long id;
    private Long orderId;
    private Integer paymentNumber;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private LocalDate paymentDate;
    private LocalDate encashmentDate;

    // Method-specific fields
    private String chequeNumber;
    private LocalDate chequeDueDate;
    private String bankName;
    private String transferReference;
    private String receiptNumber;

    private LocalDateTime createdAt;
}
