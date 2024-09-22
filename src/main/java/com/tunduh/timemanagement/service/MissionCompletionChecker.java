package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.entity.MissionEntity;
import com.tunduh.timemanagement.entity.TaskEntity;
import com.tunduh.timemanagement.entity.TaskSessionEntity;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.entity.UserMissionEntity;
import com.tunduh.timemanagement.exception.ResourceNotFoundException;
import com.tunduh.timemanagement.repository.TaskRepository;
import com.tunduh.timemanagement.repository.TaskSessionRepository;
import com.tunduh.timemanagement.repository.UserMissionRepository;
import com.tunduh.timemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionCompletionChecker {
    private static final Logger logger = LoggerFactory.getLogger(MissionCompletionChecker.class);

    private final UserMissionRepository userMissionRepository;
    private final TaskRepository taskRepository;
    private final TaskSessionRepository taskSessionRepository;
    private final UserRepository userRepository;

    @Transactional
    public void checkAllMissionsForUser(String userId) {
        List<UserMissionEntity> userMissions = userMissionRepository.findByUserId(userId);
        for (UserMissionEntity userMission : userMissions) {
            checkMissionCompletion(userId, userMission.getMission().getId());
        }
    }

    @Transactional
    public boolean checkMissionCompletion(String userId, String missionId) {
        UserMissionEntity userMission = userMissionRepository.findByMissionIdAndUserId(missionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User mission not found"));

        MissionEntity mission = userMission.getMission();
        boolean isCompleted = checkMissionCriteria(userId, mission);

        if (isCompleted && !userMission.getIsCompleted()) {
            userMission.setIsCompleted(true);
            userMission.setCompletedAt(LocalDateTime.now());
            userMissionRepository.save(userMission);
            logger.info("Mission {} completed for user {}", missionId, userId);
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
                logger.warn("Unknown mission type: {}", mission.getType());
                return false;
        }
    }

    private boolean checkTimeBasedMission(String userId, MissionEntity mission) {
        LocalDateTime missionStartTime = mission.getStartDate();
        List<TaskSessionEntity> userTaskSessions = taskSessionRepository.findByTaskUserIdAndStartTimeAfter(userId, missionStartTime);

        long totalDurationInMinutes = userTaskSessions.stream()
                .filter(session -> session.getStatus() == TaskSessionEntity.SessionStatus.COMPLETED)
                .mapToLong(TaskSessionEntity::getDurationInSeconds)
                .sum() / 60; // Convert seconds to minutes

        boolean isCompleted = totalDurationInMinutes >= mission.getCriteriaValue();
        logger.debug("Time-based mission check: User {}, Total duration: {} minutes, Required: {} minutes, Completed: {}",
                userId, totalDurationInMinutes, mission.getCriteriaValue(), isCompleted);
        return isCompleted;
    }

    private boolean checkTaskBasedMission(String userId, MissionEntity mission) {
        LocalDateTime missionStartTime = mission.getStartDate();
        long completedTasksCount = taskRepository.countByUserIdAndStatusAndCreatedAtAfter(userId, "COMPLETED", missionStartTime);
        boolean isCompleted = completedTasksCount >= mission.getCriteriaValue();
        logger.debug("Task-based mission check: User {}, Completed tasks: {}, Required: {}, Completed: {}",
                userId, completedTasksCount, mission.getCriteriaValue(), isCompleted);
        return isCompleted;
    }

    private boolean checkPointBasedMission(String userId, MissionEntity mission) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        boolean isCompleted = user.getUserPoint() >= mission.getCriteriaValue();
        logger.debug("Point-based mission check: User {}, User points: {}, Required: {}, Completed: {}",
                userId, user.getUserPoint(), mission.getCriteriaValue(), isCompleted);
        return isCompleted;
    }
}