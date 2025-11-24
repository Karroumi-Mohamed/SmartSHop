package com.smartshop.Service.impl;

import com.smartshop.DTO.Request.ClientCreateRequest;
import com.smartshop.DTO.Request.ClientUpdateRequest;
import com.smartshop.DTO.Response.ClientResponse;
import com.smartshop.Entity.Client;
import com.smartshop.Entity.User;
import com.smartshop.Enums.ClientLevel;
import com.smartshop.Enums.UserRole;
import com.smartshop.Exception.DuplicateResourceException;
import com.smartshop.Exception.ResourceNotFoundException;
import com.smartshop.Mapper.ClientMapper;
import com.smartshop.Repository.ClientRepository;
import com.smartshop.Repository.UserRepository;
import com.smartshop.Service.ClientService;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final ClientMapper clientMapper;

    @Override
    public ClientResponse create(ClientCreateRequest request) {
        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Client", "email", request.getEmail());
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("User", "username", request.getUsername());
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .role(UserRole.CLIENT)
                .build();

        User savedUser = userRepository.save(user);

        Client client = clientMapper.toEntity(request);
        client.setUser(savedUser);
        client.setLevel(ClientLevel.BASIC);
        client.setTotalOrders(0);
        client.setTotalSpent(BigDecimal.ZERO);

        Client savedClient = clientRepository.save(client);
        return clientMapper.toResponse(savedClient);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponse findById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
        return clientMapper.toResponse(client);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponse findByUserId(Long userId) {
        Client client = clientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found for user: " + userId));

        return clientMapper.toResponse(client);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientResponse> findAll() {
        return clientMapper.toResponseList(clientRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientResponse> findByLevel(ClientLevel level) {
        return clientMapper.toResponseList(clientRepository.findByLevel(level));
    }

    @Override
    public ClientResponse update(Long id, ClientUpdateRequest request) {
        Client client = clientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Client", id));

        if (request.getEmail() != null && !request.getEmail().equals(client.getEmail())) {
            if (clientRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Client", "email", request.getEmail());
            }
            client.setEmail(request.getEmail());
        }

        if (request.getName() != null) {
            client.setName(request.getName());
        }

        Client updatedClient = clientRepository.save(client);
        return clientMapper.toResponse(updatedClient);
    }

    @Override
    public void delete(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
        userRepository.delete(client.getUser());
        clientRepository.delete(client);
    }

}
