package com.smartshop.DTO.Response;

import java.time.LocalDateTime;

import com.smartshop.Enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private UserRole role;
    private LocalDateTime createdAt;
}
