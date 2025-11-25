package com.smartshop.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequest {
    private String name;
    
    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal priceExclTax;
    
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;
}
