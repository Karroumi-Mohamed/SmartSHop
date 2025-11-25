package com.smartshop.service.impl;

import com.smartshop.dto.request.LoginRequest;
import com.smartshop.dto.response.AuthResponse;
import com.smartshop.entities.User;
import com.smartshop.exception.InvalidCredentialsException;
import com.smartshop.repository.UserRepository;
import com.smartshop.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        // TODO: Use password encoder
        if (!user.getPassword().equals(request.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        // Store user in session
        httpSession.setAttribute("userId", user.getId());
        httpSession.setAttribute("userRole", user.getRole());

        return AuthResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .message("Login successful")
                .build();
    }

    @Override
    public void logout() {
        httpSession.invalidate();
    }
}
