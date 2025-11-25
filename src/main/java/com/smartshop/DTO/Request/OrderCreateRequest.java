package com.smartshop.DTO.Request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import lombok.Data;

@Data
public class OrderCreateRequest {
    @NotBlank(message = "Client ID is required")
    private Long clientId;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemRequest> items;

    @Pattern(regexp = "^PROMO-[A-Z0-9]{4}$", message = "Promo code must be in format PROMO-XXXX")
    private String promoCode;
}
