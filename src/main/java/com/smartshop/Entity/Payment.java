package com.smartshop.Entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.smartshop.Enums.PaymentMethod;
import com.smartshop.Enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payment extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false, name = "payment_number")
    private Integer paymentNumber; // sequencial number of the payment for the order ('payment 1', '2'..)

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "payment_method_type")
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "payment_status")
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Column(name = "encashment_date")
    private LocalDate encashmentDate;

    // CHEQUE DETAILS
    @Column(name = "cheque_number")
    private String chequeNumber;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "cheque_due_date")
    private LocalDate chequeDueDate;

    // VIREMENT DETAILS
    @Column(name = "transfer_reference")
    private String transferReference;

    // ESPECES DETAILS
    @Column(name = "receipt_number")
    private String receiptNumber;

}
