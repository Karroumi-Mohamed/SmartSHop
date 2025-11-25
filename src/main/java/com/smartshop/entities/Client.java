package com.smartshop.entities;

import com.smartshop.enums.CustomerTier;
import com.smartshop.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("CLIENT")
@Table(name = "clients")
@lombok.EqualsAndHashCode(callSuper = true)
public class Client extends User {
    @Column(nullable = false)
    @NotBlank(message = "Name is required")
    private String name;

    @Column(nullable = false, unique = true)
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CustomerTier tier = CustomerTier.BASIC;

    @Builder.Default
    private Integer totalOrders = 0;
    
    @Column(scale = 2, precision = 19)
    @Builder.Default
    private BigDecimal totalSpent = BigDecimal.ZERO;

    private LocalDateTime firstOrderDate;
    private LocalDateTime lastOrderDate;

    @PrePersist
    private void assignRole() {
        this.setRole(UserRole.CLIENT);
    }
}
