package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.response.UserResponse;

public interface UserService {

    UserResponse getCurrentUser(String userId);

    UserResponse addPoints(String userId, int points);
}
