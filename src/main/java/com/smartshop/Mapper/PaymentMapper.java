package com.smartshop.Mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.smartshop.DTO.Request.PaymentCreateRequest;
import com.smartshop.DTO.Response.PaymentResponse;
import com.smartshop.Entity.Payment;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    @Mapping(source = "order.id", target = "orderId")
    PaymentResponse toResponse(Payment payment);

    List<PaymentResponse> toResponseList(List<Payment> payments);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "paymentNumber", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "encashmentDate", ignore = true)
    Payment toEntity(PaymentCreateRequest request);
}
