package com.smartshop.Mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.smartshop.DTO.Response.OrderResponse;
import com.smartshop.Entity.Order;

@Mapper(componentModel = "spring", uses = { OrderItemMapper.class })
public interface OrderMapper {

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "client.name", target = "clientName")
    @Mapping(source = "orderItems", target = "items")
    @Mapping(source = "promoCode.code", target = "promoCode")
    OrderResponse toResponse(Order order);

    List<OrderResponse> toResponseList(List<Order> orders);
}
