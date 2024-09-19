package com.tunduh.timemanagement.service.impl;

import com.tunduh.timemanagement.dto.response.AdminMissionResponse;
import com.tunduh.timemanagement.dto.response.UserMissionResponse;
import com.tunduh.timemanagement.entity.MissionEntity;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.entity.UserMissionEntity;
import com.tunduh.timemanagement.exception.ResourceNotFoundException;
import com.tunduh.timemanagement.repository.MissionRepository;
import com.tunduh.timemanagement.repository.UserMissionRepository;
import com.tunduh.timemanagement.repository.UserRepository;
import com.tunduh.timemanagement.service.UserMissionService;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserMissionServiceImpl implements UserMissionService {

    private final UserMissionRepository userMissionRepository;
    private final UserRepository userRepository;
    private final MissionRepository missionRepository;

    @Override
    public CustomPagination<AdminMissionResponse> getAvailableMissions(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Fetch all missions (paginated)
        Page<MissionEntity> allMissionsPage = missionRepository.findAll(pageable);

        // Filter out claimed missions within the stream
        List<AdminMissionResponse> availableMissions = allMissionsPage.stream()
                .filter(mission -> !userMissionRepository.existsByMissionIdAndUserId(mission.getId(), userId))
                .map(this::mapToAdminMissionResponse)
                .collect(Collectors.toList());

        // Construct a CustomPagination object with filtered content and original pagination details
        return new CustomPagination<>(
                availableMissions,
                allMissionsPage.getTotalElements(),
                allMissionsPage.getNumber(),
                allMissionsPage.getSize()
        );
    }

    @Override
    public CustomPagination<UserMissionResponse> getClaimedMissions(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserMissionEntity> claimedMissionsPage = userMissionRepository.findByUserIdAndIsCompleted(userId, false, pageable);
        return new CustomPagination<>(claimedMissionsPage.map(this::mapToUserMissionResponse));
    }

    @Override
    public List<UserMissionResponse> getUserMissions(String userId) {
        List<UserMissionEntity> userMissions = userMissionRepository.findByUserId(userId);
        return userMissions.stream()
                .map(this::mapToUserMissionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserMissionResponse getUserMissionProgress(String userId, String missionId) {
        UserMissionEntity userMission = userMissionRepository.findByMissionIdAndUserId(missionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User mission not found"));
        return mapToUserMissionResponse(userMission);
    }

    @Override
    @Transactional
    public UserMissionResponse claimMission(String userId, String missionId) {
        if (userMissionRepository.existsByMissionIdAndUserId(missionId, userId)) {
            throw new IllegalStateException("Mission already claimed by this user");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        MissionEntity mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found"));

        UserMissionEntity userMission = UserMissionEntity.builder()
                .user(user)
                .mission(mission)
                .build();

        UserMissionEntity savedUserMission = userMissionRepository.save(userMission);
        return mapToUserMissionResponse(savedUserMission);
    }

    @Override
    @Transactional
    public UserMissionResponse completeMission(String userId, String missionId) {
        UserMissionEntity userMission = userMissionRepository.findByMissionIdAndUserId(missionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User mission not found"));

        if (userMission.getIsCompleted()) {
            throw new IllegalStateException("Mission already completed");
        }

        userMission.setIsCompleted(true);
        userMission.setCompletedAt(LocalDateTime.now());
        UserMissionEntity updatedUserMission = userMissionRepository.save(userMission);
        return mapToUserMissionResponse(updatedUserMission);
    }

    @Override
    @Transactional
    public UserMissionResponse claimMissionReward(String userId, String missionId) {
        UserMissionEntity userMission = userMissionRepository.findByMissionIdAndUserId(missionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User mission not found"));

        if (!userMission.getIsCompleted()) {
            throw new IllegalStateException("Mission not completed yet");
        }

        if (userMission.getIsRewardClaimed()) {
            throw new IllegalStateException("Reward already claimed");
        }

        userMission.setIsRewardClaimed(true);
        userMission.setRewardClaimedAt(LocalDateTime.now());
        UserMissionEntity updatedUserMission = userMissionRepository.save(userMission);

        // Add points to user
        UserEntity user = userMission.getUser();
        user.setUserPoint(user.getUserPoint() + userMission.getMission().getPointReward());
        userRepository.save(user);

        return mapToUserMissionResponse(updatedUserMission);
    }

    private AdminMissionResponse mapToAdminMissionResponse(MissionEntity mission) {
       return AdminMissionResponse.builder()
               .id(mission.getId())
               .name(mission.getName())
               .description(mission.getDescription())
               .criteriaValue(mission.getCriteriaValue())
               .pointReward(mission.getPointReward())
               .type(mission.getType())
               .createdAt(mission.getCreatedAt())
               .updatedAt(mission.getUpdatedAt())
               .build();
    }

    private UserMissionResponse mapToUserMissionResponse(UserMissionEntity userMission) {
        return UserMissionResponse.builder()
                .id(userMission.getId())
                .userId(userMission.getUser().getId())
                .missionId(userMission.getMission().getId())
                .missionName(userMission.getMission().getName())
                .isCompleted(userMission.getIsCompleted())
                .isRewardClaimed(userMission.getIsRewardClaimed())
                .completedAt(userMission.getCompletedAt())
                .rewardClaimedAt(userMission.getRewardClaimedAt())
                .createdAt(userMission.getCreatedAt())
                .updatedAt(userMission.getUpdatedAt())
                .build();
    }
}