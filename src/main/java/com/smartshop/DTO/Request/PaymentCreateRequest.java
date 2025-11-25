package com.smartshop.DTO.Request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import com.smartshop.Enums.PaymentMethod;

import lombok.Data;

@Data
public class PaymentCreateRequest {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @NotNull(message = "Payment date is required")
    private LocalDate paymentDate;

    private String chequeNumber;
    private LocalDate chequeDueDate;

    private String bankName;

    private String transferReference;

    private String receiptNumber;
}
