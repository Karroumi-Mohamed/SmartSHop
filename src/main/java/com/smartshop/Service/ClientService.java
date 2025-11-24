package com.smartshop.Service;

import java.util.List;

import com.smartshop.DTO.Request.ClientCreateRequest;
import com.smartshop.DTO.Request.ClientUpdateRequest;
import com.smartshop.DTO.Response.ClientResponse;
import com.smartshop.Enums.ClientLevel;

public interface ClientService {
    ClientResponse create(ClientCreateRequest request);

    ClientResponse findById(Long id);

    ClientResponse findByUserId(Long userId);

    List<ClientResponse> findAll();

    List<ClientResponse> findByLevel(ClientLevel level);

    ClientResponse update(Long id, ClientUpdateRequest request);

    void delete(Long id);
}
