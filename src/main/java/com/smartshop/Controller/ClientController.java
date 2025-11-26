package com.smartshop.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import com.smartshop.DTO.Request.ClientCreateRequest;
import com.smartshop.DTO.Request.ClientUpdateRequest;
import com.smartshop.DTO.Response.ApiResponse;
import com.smartshop.DTO.Response.ClientResponse;
import com.smartshop.DTO.Response.OrderResponse;
import com.smartshop.Enums.ClientLevel;
import com.smartshop.Service.ClientService;
import com.smartshop.Service.OrderService;
import com.smartshop.Util.AuthHelper;

import java.util.List;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;
    private final OrderService orderService;
    private final AuthHelper authHelper;

    @PostMapping
    public ResponseEntity<ApiResponse<ClientResponse>> create(
            @Valid @RequestBody ClientCreateRequest request,
            HttpSession session) {
        authHelper.requireAdmin(session);
        ClientResponse client = clientService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(client, "Client created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClientResponse>>> findAll(HttpSession session) {
        authHelper.requireAdmin(session);
        List<ClientResponse> clients = clientService.findAll();
        return ResponseEntity.ok(ApiResponse.success(clients));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientResponse>> findById(
            @PathVariable Long id,
            HttpSession session) {
        authHelper.requireAdmin(session);
        ClientResponse client = clientService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(client));
    }

    @GetMapping("/level/{level}")
    public ResponseEntity<ApiResponse<List<ClientResponse>>> findByLevel(
            @PathVariable ClientLevel level,
            HttpSession session) {
        authHelper.requireAdmin(session);
        List<ClientResponse> clients = clientService.findByLevel(level);
        return ResponseEntity.ok(ApiResponse.success(clients));
    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getClientOrders(
            @PathVariable Long id,
            HttpSession session) {
        authHelper.requireAdmin(session);
        List<OrderResponse> orders = orderService.findByClientId(id);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ClientUpdateRequest request,
            HttpSession session) {
        authHelper.requireAdmin(session);
        ClientResponse client = clientService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(client, "Client updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteClient(
            @PathVariable Long id,
            HttpSession session) {
        authHelper.requireAdmin(session);
        clientService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>success(null, "Client deleted successfully"));
    }
}
