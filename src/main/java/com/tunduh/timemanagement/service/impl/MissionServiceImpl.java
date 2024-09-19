package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.request.MissionRequest;
import com.tunduh.timemanagement.dto.response.MissionResponse;
import com.tunduh.timemanagement.entity.MissionEntity;
import com.tunduh.timemanagement.entity.SubmissionEntity;
import com.tunduh.timemanagement.entity.TaskEntity;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.entity.TransactionEntity;
import com.tunduh.timemanagement.exception.ResourceNotFoundException;
import com.tunduh.timemanagement.repository.MissionRepository;
import com.tunduh.timemanagement.repository.SubmissionRepository;
import com.tunduh.timemanagement.repository.TaskRepository;
import com.tunduh.timemanagement.repository.UserRepository;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MissionServiceImpl implements MissionService {
    private final MissionRepository missionRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final TransactionService transactionService;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional
    @CacheEvict(value = "missions", allEntries = true)
    public MissionResponse createMission(MissionRequest missionRequest) {
        log.info("Creating new mission: {}", missionRequest.getName());
        SubmissionEntity submission = submissionRepository.findById(missionRequest.getSubmissionId())
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        MissionEntity mission = MissionEntity.builder()
                .submission(submission)
                .name(missionRequest.getName())
                .description(missionRequest.getDescription())
                .status("ACTIVE")
                .pointReward(submission.getPointReward())
                .requiredTaskCount(submission.getRequiredTaskCount())
                .requiredDuration(submission.getRequiredDuration())
                .isDurationOnly(missionRequest.getIsDurationOnly())
                .isTaskOnly(missionRequest.getIsTaskOnly())
                .startDate(missionRequest.getStartDate())
                .endDate(missionRequest.getEndDate())
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
                    List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
                    if (name != null && !name.isEmpty()) {
                        predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
                    }
                    if (status != null && !status.isEmpty()) {
                        predicates.add(cb.equal(root.get("status"), status));
                    }
                    return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
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
    @Transactional
    public MissionResponse assignMissionToAllUsers(String missionId) {
        log.info("Assigning mission {} to all users", missionId);
        MissionEntity mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found"));
        List<UserEntity> allUsers = userRepository.findAll();
        mission.getUsers().addAll(allUsers);
        MissionEntity updatedMission = missionRepository.save(mission);
        log.debug("Mission {} assigned to all users", missionId);
        return mapToMissionResponse(updatedMission);
    }

    @Transactional
    public MissionResponse claimMissionReward(String missionId, String userId) {
        log.info("Claiming reward for mission {} by user {}", missionId, userId);
        MissionEntity mission = missionRepository.findByIdAndUsersId(missionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found or user not assigned"));

        if (!"COMPLETED".equals(mission.getStatus())) {
            log.error("Mission {} is not completed", missionId);
            throw new IllegalStateException("Mission is not completed");
        }

        if (mission.getIsRewardClaimed()) {
            log.error("Reward for mission {} already claimed", missionId);
            throw new IllegalStateException("Reward already claimed");
        }

        mission.setIsRewardClaimed(true);
        missionRepository.save(mission);

        // Create transaction for mission completion
        transactionService.createTransaction(userId, mission.getPointReward(),
                TransactionEntity.TransactionType.MISSION_COMPLETION,
                "Completed mission: " + mission.getName());

        log.debug("Reward claimed for mission {} by user {}", missionId, userId);
        return mapToMissionResponse(mission);
    }

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
                if (checkMissionCompletion(mission, user)) {
                    mission.setStatus("COMPLETED");
                    missionRepository.save(mission);
                    log.debug("Mission {} completed by user {}", mission.getId(), user.getId());
                    break;
                }
            }
        }
        log.info("Finished checking and updating missions");
    }

    private boolean checkMissionCompletion(MissionEntity mission, UserEntity user) {
        LocalDateTime missionStartTime = mission.getStartDate();
        List<TaskEntity> userTasks = taskRepository.findByUserAndCreatedAtAfter(user, missionStartTime);

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
                .isRewardClaimed(mission.getIsRewardClaimed())
                .createdAt(mission.getCreatedAt())
                .updatedAt(mission.getUpdatedAt())
                .build();
    }
}