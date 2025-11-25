package com.smartshop.entities;

import com.smartshop.enums.PaymentMethod;
import com.smartshop.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private Integer paymentNumber;

    @Column(nullable = false, scale = 2, precision = 19)
    @DecimalMin(value = "0.01", message = "Payment amount must be positive")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime paymentDate = LocalDateTime.now();

    private LocalDateTime encashmentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.EN_ATTENTE;

    // Fields for ESPECES
    private String receiptNumber;

    // Fields for CHEQUE
    private String checkNumber;
    private String bank;
    private LocalDateTime dueDate;

    // Fields for VIREMENT
    private String transferReference;

    @PrePersist
    private void prePersist() {
        if (this.paymentDate == null) {
            this.paymentDate = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = PaymentStatus.EN_ATTENTE;
        }
        
        // Auto-encash ESPECES
        if (PaymentMethod.ESPECES.equals(this.paymentMethod)) {
            this.status = PaymentStatus.ENCAISSE;
            this.encashmentDate = LocalDateTime.now();
        }
    }
}
