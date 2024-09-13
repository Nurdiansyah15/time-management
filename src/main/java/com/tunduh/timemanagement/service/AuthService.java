package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.request.RegisterRequest;
import com.tunduh.timemanagement.entity.UserEntity;

public interface AuthService {
    boolean existsByEmail(String email);

    UserEntity registerUser(RegisterRequest registerRequest);

    UserEntity getOrCreateUser(String email, String name);

    String createToken(UserEntity user);
}
