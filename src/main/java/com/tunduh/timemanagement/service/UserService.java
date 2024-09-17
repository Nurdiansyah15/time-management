package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.response.TaskResponse;
import com.tunduh.timemanagement.dto.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserResponse getCurrentUser(String userId);

    UserResponse addPoints(String userId, int points);

    UserResponse updatePhoto(MultipartFile files, String id);
}
