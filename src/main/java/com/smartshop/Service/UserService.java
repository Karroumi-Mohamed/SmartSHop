package com.smartshop.Service;

import java.util.List;

import com.smartshop.DTO.Request.LoginRequest;
import com.smartshop.DTO.Request.UserCreateRequest;
import com.smartshop.DTO.Response.UserResponse;

public interface UserService {
    UserResponse create(UserCreateRequest request);

    UserResponse findById(Long id);

    UserResponse findByUsername(String username);

    List<UserResponse> findAll();

    UserResponse authenticate(LoginRequest request);
}
