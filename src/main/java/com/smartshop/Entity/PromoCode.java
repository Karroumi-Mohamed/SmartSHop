package com.smartshop.Entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "promocodes")
public class PromoCode extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false, precision = 5, scale = 2, name = "discount_percentage")
    @Builder.Default
    private BigDecimal discountPercentage = new BigDecimal("5.00");

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Builder.Default
    private Boolean active = true;

    @Builder.Default
    private Boolean used = false;

}
