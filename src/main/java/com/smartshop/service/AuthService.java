package com.smartshop.service;

import com.smartshop.dto.request.LoginRequest;
import com.smartshop.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    void logout();
}
