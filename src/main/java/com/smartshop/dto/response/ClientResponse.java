package com.smartshop.dto.response;

import com.smartshop.enums.CustomerTier;
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
public class ClientResponse {
    private Long id;
    private String username;
    private String name;
    private String email;
    private CustomerTier tier;
    private Integer totalOrders;
    private BigDecimal totalSpent;
    private LocalDateTime firstOrderDate;
    private LocalDateTime lastOrderDate;
}
