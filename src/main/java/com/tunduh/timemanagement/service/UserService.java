package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.response.UserResponse;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.exception.ResourceNotFoundException;
import com.tunduh.timemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getCurrentUser(String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToUserResponse(user);
    }

    @Transactional
    public UserResponse addPoints(String userId, int points) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setUserPoint(user.getUserPoint() + points);
        UserEntity updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }

    private UserResponse mapToUserResponse(UserEntity user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .userPoint(user.getUserPoint())
                .build();
    }
}