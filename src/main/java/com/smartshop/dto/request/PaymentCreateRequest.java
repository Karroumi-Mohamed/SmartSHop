package com.smartshop.dto.request;

import com.smartshop.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
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
public class PaymentCreateRequest {
    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Payment amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    // Fields for ESPECES
    private String receiptNumber;

    // Fields for CHEQUE
    private String checkNumber;
    private String bank;
    private LocalDateTime dueDate;

    // Fields for VIREMENT
    private String transferReference;
}
