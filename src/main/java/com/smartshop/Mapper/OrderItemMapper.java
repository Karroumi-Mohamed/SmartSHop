package com.smartshop.Mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.smartshop.DTO.Response.OrderItemResponse;
import com.smartshop.Entity.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    OrderItemResponse toResponse(OrderItem orderItem);

    List<OrderItemResponse> toResponseList(List<OrderItem> orderItems);

}
