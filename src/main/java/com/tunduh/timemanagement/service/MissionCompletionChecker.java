package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.entity.MissionEntity;
import com.tunduh.timemanagement.entity.TaskEntity;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.entity.UserMissionEntity;
import com.tunduh.timemanagement.repository.TaskRepository;
import com.tunduh.timemanagement.repository.UserMissionRepository;
import com.tunduh.timemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionCompletionChecker {
    private final UserMissionRepository userMissionRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public void checkAllMissionsForUser(String userId) {
        List<UserMissionEntity> userMissions = userMissionRepository.findByUserId(userId);
        for (UserMissionEntity userMission : userMissions) {
            checkMissionCompletion(userId, userMission.getMission().getId());
        }
    }

    public boolean checkMissionCompletion(String userId, String missionId) {
        UserMissionEntity userMission = userMissionRepository.findByMissionIdAndUserId(missionId, userId)
                .orElseThrow(() -> new RuntimeException("User mission not found"));

        MissionEntity mission = userMission.getMission();
        boolean isCompleted = checkMissionCriteria(userId, mission);

        if (isCompleted && !userMission.getIsCompleted()) {
            userMission.setIsCompleted(true);
            userMission.setCompletedAt(LocalDateTime.now());
            userMissionRepository.save(userMission);
        }

        return isCompleted;
    }

    private boolean checkMissionCriteria(String userId, MissionEntity mission) {
        switch (mission.getType()) {
            case TIME_BASED:
                return checkTimeBasedMission(userId, mission);
            case TASK_BASED:
                return checkTaskBasedMission(userId, mission);
            case POINT_BASED:
                return checkPointBasedMission(userId, mission);
            default:
                return false;
        }
    }

    private boolean checkTimeBasedMission(String userId, MissionEntity mission) {
        LocalDateTime missionStartTime = mission.getStartDate();
        List<TaskEntity> userTasks = taskRepository.findByUserAndCreatedAtAfter(
                userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")),
                missionStartTime
        );

        long totalDurationInMinutes = userTasks.stream()
                .mapToLong(task -> Duration.between(task.getCreatedAt(), LocalDateTime.now()).toMinutes())
                .sum();

        return totalDurationInMinutes >= mission.getCriteriaValue();
    }

    private boolean checkTaskBasedMission(String userId, MissionEntity mission) {
        LocalDateTime missionStartTime = mission.getStartDate();
        long completedTasksCount = taskRepository.countByUserIdAndStatus(userId, "COMPLETED");
        return completedTasksCount >= mission.getCriteriaValue();
    }

    private boolean checkPointBasedMission(String userId, MissionEntity mission) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getUserPoint() >= mission.getCriteriaValue();
    }
}