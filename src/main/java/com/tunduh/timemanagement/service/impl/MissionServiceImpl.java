package com.tunduh.timemanagement.service.impl;

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
    @Transactional
    public MissionResponse claimMission(String id, String userId) {
        log.info("User {} claiming mission {}", userId, id);
        MissionEntity mission = missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found"));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (mission.getIsClaimed()) {
            throw new IllegalStateException("Mission already claimed");
        }

        if (mission.getStartDate().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("Mission has not started yet");
        }

        if (mission.getEndDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Mission has already ended");
        }

        mission.getUsers().add(user);
        mission.setIsClaimed(true);
        MissionEntity updatedMission = missionRepository.save(mission);

        log.debug("Mission {} claimed by user {}", id, userId);
        return mapToMissionResponse(updatedMission);
    }

    @Override
    @Transactional
    public MissionResponse claimMissionReward(String id, String userId) {
        log.info("User {} claiming reward for mission {}", userId, id);
        MissionEntity mission = missionRepository.findByIdAndUsersId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found or not claimed by user"));

        if (!"COMPLETED".equals(mission.getStatus())) {
            throw new IllegalStateException("Mission is not completed");
        }

        if (mission.getIsRewardClaimed()) {
            throw new IllegalStateException("Reward already claimed");
        }

        if (!checkMissionCompletion(mission, userId)) {
            throw new IllegalStateException("Mission requirements not met");
        }

        mission.setIsRewardClaimed(true);
        missionRepository.save(mission);

        transactionService.createTransaction(userId, mission.getPointReward(),
                TransactionEntity.TransactionType.MISSION_COMPLETION,
                "Completed mission: " + mission.getName());

        log.debug("Reward claimed for mission {} by user {}", id, userId);
        return mapToMissionResponse(mission);
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