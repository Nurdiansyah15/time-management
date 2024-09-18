package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.request.UserUpdateRequest;
import com.tunduh.timemanagement.dto.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;

public interface UserService {
    UserResponse getCurrentUser(String userId);
    UserResponse addPoints(String userId, int points);
    UserResponse updatePhoto(MultipartFile file, String id);
    UserResponse updateUser(String userId, UserUpdateRequest updateRequest);
    UserResponse setResetTime(String userId, LocalTime resetTime);
    void performDailyReset();
}