package com.smartshop.DTO.Response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.smartshop.Enums.ClientLevel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {
    private Long id;
    private String name;
    private String email;
    private ClientLevel level;
    private Integer totalOrders;
    private BigDecimal totalSpent;
    private LocalDateTime firstOrderDate;
    private LocalDateTime lastOrderDate;
    private LocalDateTime createdAt;

    private Long userId;
    private String username;

}
