package com.smartshop.mapper;

import com.smartshop.dto.response.OrderResponse;
import com.smartshop.dto.response.OrderSummaryResponse;
import com.smartshop.entities.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {
    
    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "client.name", target = "clientName")
    OrderResponse toResponse(Order order);
    
    OrderSummaryResponse toSummaryResponse(Order order);
}
