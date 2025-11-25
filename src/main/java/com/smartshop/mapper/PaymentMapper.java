package com.smartshop.mapper;

import com.smartshop.dto.request.PaymentCreateRequest;
import com.smartshop.dto.response.PaymentResponse;
import com.smartshop.entities.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "paymentNumber", ignore = true)
    @Mapping(target = "paymentDate", ignore = true)
    @Mapping(target = "encashmentDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    Payment toEntity(PaymentCreateRequest request);
    
    @Mapping(source = "order.id", target = "orderId")
    PaymentResponse toResponse(Payment payment);
}
