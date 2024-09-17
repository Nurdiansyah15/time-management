package com.tunduh.timemanagement.service.impl;

import com.tunduh.timemanagement.dto.response.TaskResponse;
import com.tunduh.timemanagement.dto.response.UserResponse;
import com.tunduh.timemanagement.entity.TaskEntity;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.exception.ResourceNotFoundException;
import com.tunduh.timemanagement.repository.UserRepository;
import com.tunduh.timemanagement.service.CloudinaryService;
import com.tunduh.timemanagement.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;


    @Override
    public UserResponse getCurrentUser(String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse addPoints(String userId, int points) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setUserPoint(user.getUserPoint() + points);
        UserEntity updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }

    @Override
    @jakarta.transaction.Transactional
    public UserResponse updatePhoto(MultipartFile file, String id) {
        UserEntity userItem = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User item with id " + id + " not found"));
        String url = cloudinaryService.uploadFile(file, "user");
        userItem.setProfilePicture(url);
        UserEntity savedUserItem = userRepository.save(userItem);
        return mapToUserResponse(savedUserItem);
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
