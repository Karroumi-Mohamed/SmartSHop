package com.smartshop.service;

import com.smartshop.dto.request.ClientCreateRequest;
import com.smartshop.dto.request.ClientUpdateRequest;
import com.smartshop.dto.response.ClientResponse;
import com.smartshop.dto.response.OrderSummaryResponse;
import com.smartshop.enums.CustomerTier;

import java.util.List;

public interface ClientService {
    ClientResponse createClient(ClientCreateRequest request);
    ClientResponse getClientById(Long id);
    ClientResponse updateClient(Long id, ClientUpdateRequest request);
    void deleteClient(Long id);
    List<ClientResponse> getAllClients();
    List<OrderSummaryResponse> getClientOrderHistory(Long clientId);
    CustomerTier calculateTier(Integer totalOrders, java.math.BigDecimal totalSpent);
    void updateClientTier(Long clientId);
}
