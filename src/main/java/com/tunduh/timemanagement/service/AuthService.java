package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.request.LoginRequest;
import com.tunduh.timemanagement.dto.request.RegisterRequest;
import com.tunduh.timemanagement.dto.response.AuthResponse;
import com.tunduh.timemanagement.entity.UserEntity;

public interface AuthService {
    boolean existsByEmail(String email);

    AuthResponse login(LoginRequest req);

    AuthResponse register(RegisterRequest req);

    AuthResponse refreshToken(String refreshToken);

}
