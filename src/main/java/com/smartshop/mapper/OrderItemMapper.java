package com.smartshop.mapper;

import com.smartshop.dto.response.OrderItemResponse;
import com.smartshop.entities.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    OrderItemResponse toResponse(OrderItem orderItem);
}
