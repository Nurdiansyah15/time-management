package com.tunduh.timemanagement.service.impl;

import com.tunduh.timemanagement.dto.request.UserUpdateRequest;
import com.tunduh.timemanagement.dto.response.UserResponse;
import com.tunduh.timemanagement.entity.MissionEntity;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.exception.ResourceNotFoundException;
import com.tunduh.timemanagement.repository.MissionRepository;
import com.tunduh.timemanagement.repository.UserRepository;
import com.tunduh.timemanagement.service.CloudinaryService;
import com.tunduh.timemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final MissionRepository missionRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    @Cacheable(value = "user", key = "#userId")
    public UserResponse getCurrentUser(String userId) {
        UserEntity user = getUserById(userId);
        long claimedMissions = missionRepository.countByUserIdAndIsCompleted(userId, true);
        long completedMissions = missionRepository.countByUserIdAndStatus(userId, MissionEntity.MissionStatus.COMPLETED);
        long unclaimedRewards = missionRepository.countByUserIdAndStatusAndIsRewardClaimedFalse(userId, MissionEntity.MissionStatus.COMPLETED);

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .userPoint(user.getUserPoint())
                .resetTime(user.getResetTime())
                .claimedMissions(claimedMissions)
                .completedMissions(completedMissions)
                .unclaimedRewards(unclaimedRewards)
                .build();
    }

    @Override
    @Transactional
    @CacheEvict(value = "user", key = "#userId")
    public UserResponse addPoints(String userId, int points) {
        UserEntity user = getUserById(userId);
        user.setUserPoint(user.getUserPoint() + points);
        UserEntity updatedUser = userRepository.save(user);
        logger.info("Added {} points to user {}", points, userId);
        return mapToUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public UserResponse updatePhoto(MultipartFile file, String id) {
        UserEntity user = getUserById(id);
        String url = cloudinaryService.uploadFile(file, "user");
        user.setProfilePicture(url);
        UserEntity updatedUser = userRepository.save(user);
        logger.info("Updated profile picture for user {}", id);
        return mapToUserResponse(updatedUser);
    }

    @Override
    @Transactional
    @CacheEvict(value = "user", key = "#userId")
    public UserResponse updateUser(String userId, UserUpdateRequest updateRequest) {
        UserEntity user = getUserById(userId);
        user.setUsername(updateRequest.getUsername());
        user.setEmail(updateRequest.getEmail());
        UserEntity updatedUser = userRepository.save(user);
        logger.info("Updated user information for user {}", userId);
        return mapToUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public UserResponse setResetTime(String userId, LocalTime resetTime) {
        UserEntity user = getUserById(userId);
        user.setResetTime(resetTime);
        UserEntity updatedUser = userRepository.save(user);
        logger.info("Set reset time to {} for user {}", resetTime, userId);
        return mapToUserResponse(updatedUser);
    }

    @Override
    @Scheduled(cron = "0 * * * * *") // Run every minute
    @Transactional
    public void performDailyReset() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        List<UserEntity> usersToReset = userRepository.findByResetTimeLessThanEqualAndLastResetDateBefore(now, today);

        for (UserEntity user : usersToReset) {
            // Perform reset logic here
            // For example, reset daily tasks, update streak, etc.
            user.setLastResetDate(today);
            userRepository.save(user);
            logger.info("Performed daily reset for user {}", user.getId());
        }
    }

    private UserEntity getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private UserResponse mapToUserResponse(UserEntity user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .userPoint(user.getUserPoint())
                .resetTime(user.getResetTime())
                .build();
    }
}