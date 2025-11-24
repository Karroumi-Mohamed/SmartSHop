package com.smartshop.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.smartshop.Enums.OrderStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(nullable = false, name = "order_date")
    @Builder.Default
    private LocalDateTime orderDate = LocalDateTime.now();

    @Column(nullable = false, scale = 2, precision = 10)
    private BigDecimal subTotal; // HT

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promo_code_id")
    private PromoCode promoCode;

    @Column(scale = 2, precision = 5, name = "discount_percentage")
    @Builder.Default
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @Column(nullable = false, scale = 2, precision = 10)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(scale = 2, precision = 10, name = "amount_after_discount")
    private BigDecimal amountAfterDiscount;

    @Column(nullable = false, scale = 2, precision = 10, name = "tva_amount")
    private BigDecimal tvaAmount;

    @Builder.Default
    @Column(nullable = false, scale = 2, precision = 5, name = "tva_rate")
    private BigDecimal tvaRate = new BigDecimal("20.00");

    @Column(nullable = false, scale = 2, precision = 10, name = "total_ttc")
    private BigDecimal totalTTC;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "remaining_amount", nullable = false, scale = 2, precision = 10)
    private BigDecimal remainingAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();

    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }

    public void removeOrderItem(OrderItem item) {
        orderItems.remove(item);
        item.setOrder(null);
    }
}
