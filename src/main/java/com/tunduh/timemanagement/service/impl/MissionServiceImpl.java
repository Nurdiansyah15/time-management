package com.tunduh.timemanagement.service.impl;

import com.tunduh.timemanagement.dto.request.MissionRequest;
import com.tunduh.timemanagement.dto.response.MissionProgressResponse;
import com.tunduh.timemanagement.dto.response.MissionResponse;
import com.tunduh.timemanagement.entity.*;
import com.tunduh.timemanagement.exception.ResourceNotFoundException;
import com.tunduh.timemanagement.repository.MissionRepository;
import com.tunduh.timemanagement.repository.TaskRepository;
import com.tunduh.timemanagement.repository.UserMissionRepository;
import com.tunduh.timemanagement.repository.UserRepository;
import com.tunduh.timemanagement.service.CloudinaryService;
import com.tunduh.timemanagement.service.MissionService;
import com.tunduh.timemanagement.service.TransactionService;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MissionServiceImpl implements MissionService {
    private final MissionRepository missionRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final TransactionService transactionService;
    private final CloudinaryService cloudinaryService;
    private final UserMissionRepository userMissionRepository;

    @Override
    @Transactional
    @CacheEvict(value = "missions", allEntries = true)
    public MissionResponse createMission(MissionRequest missionRequest) {
        log.info("Creating new mission: {}", missionRequest.getName());

        MissionEntity mission = MissionEntity.builder()
                .name(missionRequest.getName())
                .description(missionRequest.getDescription())
                .status("ACTIVE")
                .pointReward(missionRequest.getPointReward())
                .requiredTaskCount(missionRequest.getRequiredTaskCount())
                .requiredDuration(missionRequest.getRequiredDuration())
                .isDurationOnly(missionRequest.getIsDurationOnly())
                .isTaskOnly(missionRequest.getIsTaskOnly())
                .startDate(missionRequest.getStartDate())
                .endDate(missionRequest.getEndDate())
                .isClaimed(false)
                .isRewardClaimed(false)
                .build();

        MissionEntity savedMission = missionRepository.save(mission);
        log.debug("Mission created with ID: {}", savedMission.getId());
        return mapToMissionResponse(savedMission);
    }

    @Override
    @Transactional
    public MissionResponse updatePhoto(MultipartFile file, String id) {
        log.info("Updating photo for mission with ID: {}", id);
        MissionEntity mission = missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found"));

        String url = cloudinaryService.uploadFile(file, "mission");
        mission.setMissionPicture(url);
        MissionEntity updatedMission = missionRepository.save(mission);
        log.debug("Mission photo updated for ID: {}", id);
        return mapToMissionResponse(updatedMission);
    }

    @Override
    @Cacheable(value = "missions")
    public CustomPagination<MissionResponse> getAllMissions(int page, int size, String sort, String name, String status) {
        log.info("Fetching missions with page: {}, size: {}, sort: {}, name: {}, status: {}", page, size, sort, name, status);
        Sort.Direction direction = Sort.Direction.ASC;
        if (sort.startsWith("-")) {
            direction = Sort.Direction.DESC;
            sort = sort.substring(1);
        }
        Page<MissionEntity> missionPage = missionRepository.findAll(
                (root, query, cb) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    if (name != null && !name.isEmpty()) {
                        predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
                    }
                    if (status != null && !status.isEmpty()) {
                        predicates.add(cb.equal(root.get("status"), status));
                    }
                    return cb.and(predicates.toArray(new Predicate[0]));
                },
                PageRequest.of(page, size, Sort.by(direction, sort))
        );
        log.debug("Fetched {} missions", missionPage.getTotalElements());
        return new CustomPagination<>(missionPage.map(this::mapToMissionResponse));
    }

    @Override
    @Cacheable(value = "mission", key = "#id")
    public MissionResponse getMissionById(String id) {
        log.info("Fetching mission with ID: {}", id);
        MissionEntity mission = missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found"));
        return mapToMissionResponse(mission);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"missions", "mission"}, key = "#id")
    public MissionResponse updateMission(String id, MissionRequest missionRequest) {
        log.info("Updating mission with ID: {}", id);
        MissionEntity mission = missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found"));

        mission.setName(missionRequest.getName());
        mission.setDescription(missionRequest.getDescription());
        mission.setPointReward(missionRequest.getPointReward());
        mission.setRequiredTaskCount(missionRequest.getRequiredTaskCount());
        mission.setRequiredDuration(missionRequest.getRequiredDuration());
        mission.setIsDurationOnly(missionRequest.getIsDurationOnly());
        mission.setIsTaskOnly(missionRequest.getIsTaskOnly());
        mission.setStartDate(missionRequest.getStartDate());
        mission.setEndDate(missionRequest.getEndDate());

        MissionEntity updatedMission = missionRepository.save(mission);
        log.debug("Mission updated with ID: {}", id);
        return mapToMissionResponse(updatedMission);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"missions", "mission"}, key = "#id")
    public void deleteMission(String id) {
        log.info("Deleting mission with ID: {}", id);
        MissionEntity mission = missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found"));
        missionRepository.delete(mission);
        log.debug("Mission deleted with ID: {}", id);
    }

    @Override
    public List<MissionResponse> getAvailableMissionsForUser(String userId) {
        log.info("Fetching available missions for user: {}", userId);
        LocalDateTime now = LocalDateTime.now();
        List<MissionEntity> availableMissions = missionRepository.findAvailableMissionsForUser(userId, now);
        return availableMissions.stream()
                .map(this::mapToMissionResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void checkAndUpdateMissions() {
        log.info("Checking and updating missions");
        List<MissionEntity> activeMissions = missionRepository.findByStatus("ACTIVE");
        LocalDateTime now = LocalDateTime.now();

        for (MissionEntity mission : activeMissions) {
            if (now.isAfter(mission.getEndDate())) {
                mission.setStatus("EXPIRED");
                missionRepository.save(mission);
                log.debug("Mission {} expired", mission.getId());
                continue;
            }

            for (UserEntity user : mission.getUsers()) {
                if (checkMissionCompletion(mission, user.getId())) {
                    mission.setStatus("COMPLETED");
                    missionRepository.save(mission);
                    log.debug("Mission {} completed by user {}", mission.getId(), user.getId());
                    break;
                }
            }
        }
        log.info("Finished checking and updating missions");
    }

    private boolean checkMissionCompletion(MissionEntity mission, String userId) {
        LocalDateTime missionStartTime = mission.getStartDate();
        List<TaskEntity> userTasks = taskRepository.findByUserIdAndCreatedAtBetween(
                userId, missionStartTime, LocalDateTime.now());

        int completedTaskCount = (int) userTasks.stream()
                .filter(task -> "COMPLETED".equals(task.getStatus()))
                .count();

        long totalDuration = userTasks.stream()
                .filter(task -> "COMPLETED".equals(task.getStatus()))
                .mapToLong(TaskEntity::getDuration)
                .sum();

        log.debug("Mission completion check - Completed tasks: {}, Total duration: {}", completedTaskCount, totalDuration);

        if (mission.getIsDurationOnly()) {
            return totalDuration >= mission.getRequiredDuration();
        } else if (mission.getIsTaskOnly()) {
            return completedTaskCount >= mission.getRequiredTaskCount();
        } else {
            return completedTaskCount >= mission.getRequiredTaskCount() && totalDuration >= mission.getRequiredDuration();
        }
    }
    @Override
    public MissionProgressResponse getMissionProgress(String missionId, String userId) {
        log.info("Fetching progress for mission: {} and user: {}", missionId, userId);
        MissionEntity mission = missionRepository.findByIdAndUsersId(missionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found or not claimed by user"));

        List<TaskEntity> userTasks = taskRepository.findByUserIdAndCreatedAtBetween(
                userId, mission.getStartDate(), LocalDateTime.now());

        int completedTasks = (int) userTasks.stream()
                .filter(task -> "COMPLETED".equals(task.getStatus()))
                .count();

        long completedDuration = userTasks.stream()
                .filter(task -> "COMPLETED".equals(task.getStatus()))
                .mapToLong(TaskEntity::getDuration)
                .sum();

        double progressPercentage;
        if (mission.getIsDurationOnly()) {
            progressPercentage = (double) completedDuration / mission.getRequiredDuration() * 100;
        } else if (mission.getIsTaskOnly()) {
            progressPercentage = (double) completedTasks / mission.getRequiredTaskCount() * 100;
        } else {
            double taskProgress = (double) completedTasks / mission.getRequiredTaskCount();
            double durationProgress = (double) completedDuration / mission.getRequiredDuration();
            progressPercentage = (taskProgress + durationProgress) / 2 * 100;
        }

        return MissionProgressResponse.builder()
                .missionId(mission.getId())
                .missionName(mission.getName())
                .completedTasks(completedTasks)
                .totalRequiredTasks(mission.getRequiredTaskCount())
                .completedDuration(completedDuration)
                .totalRequiredDuration(mission.getRequiredDuration())
                .progressPercentage(Math.min(progressPercentage, 100))
                .build();
    }

    @Override
    @Transactional
    public MissionResponse claimMission(String missionId, String userId) {
        MissionEntity mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found"));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (userMissionRepository.existsByMissionIdAndUserId(missionId, userId)) {
            throw new IllegalStateException("Mission already claimed by this user");
        }

        UserMissionEntity userMission = UserMissionEntity.builder()
                .user(user)
                .mission(mission)
                .build();

        userMissionRepository.save(userMission);

        return mapToMissionResponse(mission);
    }

    @Override
    @Transactional
    public MissionResponse completeMission(String missionId, String userId) {
        UserMissionEntity userMission = userMissionRepository.findByMissionIdAndUserId(missionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User mission not found"));

        if (userMission.getIsCompleted()) {
            throw new IllegalStateException("Mission already completed");
        }

        userMission.setIsCompleted(true);
        userMission.setCompletedAt(LocalDateTime.now());
        userMissionRepository.save(userMission);

        return mapToMissionResponse(userMission.getMission());
    }

    @Override
    @Transactional
    public MissionResponse claimMissionReward(String missionId, String userId) {
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
        userMissionRepository.save(userMission);

        // Add points to user
        UserEntity user = userMission.getUser();
        user.setUserPoint(user.getUserPoint() + userMission.getMission().getPointReward());
        userRepository.save(user);

        return mapToMissionResponse(userMission.getMission());
    }

    private MissionResponse mapToMissionResponse(MissionEntity mission) {
        return MissionResponse.builder()
                .id(mission.getId())
                .name(mission.getName())
                .description(mission.getDescription())
                .status(mission.getStatus())
                .pointReward(mission.getPointReward())
                .missionPicture(mission.getMissionPicture())
                .requiredTaskCount(mission.getRequiredTaskCount())
                .requiredDuration(mission.getRequiredDuration())
                .isDurationOnly(mission.getIsDurationOnly())
                .isTaskOnly(mission.getIsTaskOnly())
                .startDate(mission.getStartDate())
                .endDate(mission.getEndDate())
                .isClaimed(mission.getIsClaimed())
                .isRewardClaimed(mission.getIsRewardClaimed())
                .createdAt(mission.getCreatedAt())
                .updatedAt(mission.getUpdatedAt())
                .build();
    }
}