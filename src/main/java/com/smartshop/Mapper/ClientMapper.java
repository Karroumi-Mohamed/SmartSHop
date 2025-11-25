package com.smartshop.Mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.smartshop.DTO.Request.ClientCreateRequest;
import com.smartshop.DTO.Response.ClientResponse;
import com.smartshop.Entity.Client;

@Mapper(componentModel = "string")
public interface ClientMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    ClientResponse toResponse(Client client);

    List<ClientResponse> toResponseList(List<Client> clients);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "level", ignore = true)
    @Mapping(target = "totalOrders", ignore = true)
    @Mapping(target = "totalSpent", ignore = true)
    @Mapping(target = "firstOrderDate", ignore = true)
    @Mapping(target = "lastOrderDate", ignore = true)
    Client toEntity(ClientCreateRequest request);
}
