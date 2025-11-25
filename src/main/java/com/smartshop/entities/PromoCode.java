package com.smartshop.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "promo_codes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "PROMO-[A-Z0-9]{4}", message = "Promo code must follow format PROMO-XXXX")
    private String code;

    @Column(nullable = false, scale = 2, precision = 5)
    @DecimalMin(value = "0.0", message = "Discount must be at least 0%")
    @DecimalMax(value = "100.0", message = "Discount cannot exceed 100%")
    @Builder.Default
    private BigDecimal discountPercentage = new BigDecimal("5.00");

    @Builder.Default
    private Boolean active = true;

    @ElementCollection
    @CollectionTable(name = "promo_code_usage", joinColumns = @JoinColumn(name = "promo_code_id"))
    @Column(name = "client_id")
    @Builder.Default
    private Set<Long> usedByClients = new HashSet<>();

    public boolean hasBeenUsedBy(Long clientId) {
        return usedByClients.contains(clientId);
    }

    public void markAsUsedBy(Long clientId) {
        usedByClients.add(clientId);
    }
}
