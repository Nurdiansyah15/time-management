package com.tunduh.timemanagement.service.impl;

import com.tunduh.timemanagement.dto.request.MissionRequest;
import com.tunduh.timemanagement.dto.response.MissionResponse;
import com.tunduh.timemanagement.entity.MissionEntity;
import com.tunduh.timemanagement.entity.TaskEntity;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.entity.TransactionEntity;
import com.tunduh.timemanagement.exception.ResourceNotFoundException;
import com.tunduh.timemanagement.repository.MissionRepository;
import com.tunduh.timemanagement.repository.TaskRepository;
import com.tunduh.timemanagement.repository.UserRepository;
import com.tunduh.timemanagement.service.CloudinaryService;
import com.tunduh.timemanagement.service.MissionService;
import com.tunduh.timemanagement.service.TransactionService;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionServiceImpl implements MissionService {
    private final MissionRepository missionRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final TransactionService transactionService;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional
    public MissionResponse createMission(MissionRequest missionRequest) {
        MissionEntity mission = MissionEntity.builder()
                .name(missionRequest.getName())
                .description(missionRequest.getDescription())
                .status("ACTIVE")
                .pointReward(missionRequest.getPointReward())
                .requiredTaskCount(missionRequest.getRequiredTaskCount())
                .requiredDuration(missionRequest.getRequiredDuration())
                .isDurationOnly(missionRequest.getIsDurationOnly())
                .isTaskOnly(missionRequest.getIsTaskOnly())
                .build();

        MissionEntity savedMission = missionRepository.save(mission);
        return mapToMissionResponse(savedMission);
    }

    @Override
    @Transactional
    public MissionResponse updatePhoto(MultipartFile file, String id) {
        MissionEntity mission = missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found"));
        String url = cloudinaryService.uploadFile(file, "mission");
        mission.setMissionPicture(url);
        MissionEntity updatedMission = missionRepository.save(mission);
        return mapToMissionResponse(updatedMission);
    }

    @Override
    public CustomPagination<MissionResponse> getAllMissions(int page, int size, String sort, String name, String status) {
        Sort.Direction direction = Sort.Direction.ASC;
        if (sort.startsWith("-")) {
            direction = Sort.Direction.DESC;
            sort = sort.substring(1);
        }
        Page<MissionEntity> missionPage = missionRepository.findAll(
                (root, query, cb) -> {
                    Predicate predicate = cb.conjunction();
                    if (name != null && !name.isEmpty()) {
                        predicate = cb.and(predicate, cb.like(root.get("name"), "%" + name + "%"));
                    }
                    if (status != null && !status.isEmpty()) {
                        predicate = cb.and(predicate, cb.equal(root.get("status"), status));
                    }
                    return predicate;
                },
                PageRequest.of(page, size, Sort.by(direction, sort))
        );
        return new CustomPagination<>(missionPage.map(this::mapToMissionResponse));
    }

    @Override
    public MissionResponse getMissionById(String id) {
        MissionEntity mission = missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found"));
        return mapToMissionResponse(mission);
    }

    @Override
    @Transactional
    public MissionResponse updateMission(String id, MissionRequest missionRequest) {
        MissionEntity mission = missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found"));
        mission.setName(missionRequest.getName());
        mission.setDescription(missionRequest.getDescription());
        mission.setPointReward(missionRequest.getPointReward());
        mission.setRequiredTaskCount(missionRequest.getRequiredTaskCount());
        mission.setRequiredDuration(missionRequest.getRequiredDuration());
        mission.setIsDurationOnly(missionRequest.getIsDurationOnly());
        mission.setIsTaskOnly(missionRequest.getIsTaskOnly());
        MissionEntity updatedMission = missionRepository.save(mission);
        return mapToMissionResponse(updatedMission);
    }

    @Override
    @Transactional
    public void deleteMission(String id) {
        MissionEntity mission = missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found"));
        missionRepository.delete(mission);
    }

    @Override
    @Transactional
    public MissionResponse claimMission(String id, String userId) {
        MissionEntity mission = missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found"));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!mission.getUsers().contains(user)) {
            throw new IllegalStateException("User is not assigned to this mission");
        }

        if (!"ACTIVE".equals(mission.getStatus())) {
            throw new IllegalStateException("Mission is not active");
        }

        boolean isCompleted = checkMissionCompletion(mission, user);
        if (!isCompleted) {
            throw new IllegalStateException("Mission requirements not met");
        }

        mission.setStatus("COMPLETED");
        missionRepository.save(mission);

        // Award points to the user
        transactionService.createTransaction(userId, mission.getPointReward(),
                TransactionEntity.TransactionType.MISSION_COMPLETION,
                "Completed mission: " + mission.getName());

        return mapToMissionResponse(mission);
    }

    @Override
    @Transactional
    public MissionResponse assignMissionToUser(String missionId, String userId) {
        MissionEntity mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found"));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        mission.getUsers().add(user);
        MissionEntity updatedMission = missionRepository.save(mission);
        return mapToMissionResponse(updatedMission);
    }

    private boolean checkMissionCompletion(MissionEntity mission, UserEntity user) {
        LocalDateTime missionStartTime = mission.getCreatedAt();
        List<TaskEntity> userTasks = taskRepository.findByUserAndCreatedAtAfter(user, missionStartTime);

        int completedTaskCount = (int) userTasks.stream()
                .filter(task -> "COMPLETED".equals(task.getStatus()))
                .count();

        long totalDuration = userTasks.stream()
                .filter(task -> "COMPLETED".equals(task.getStatus()))
                .mapToLong(TaskEntity::getDuration)
                .sum();

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
                .createdAt(mission.getCreatedAt())
                .updatedAt(mission.getUpdatedAt())
                .build();
    }
}