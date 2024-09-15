package com.tunduh.timemanagement.service.impl;

import com.tunduh.timemanagement.dto.response.UserAnalyticsResponse;
import com.tunduh.timemanagement.repository.TaskRepository;
import com.tunduh.timemanagement.repository.MissionRepository;
import com.tunduh.timemanagement.repository.UserTransactionRepository;
import com.tunduh.timemanagement.service.UserAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserAnalyticsServiceImpl implements UserAnalyticsService {

    private final TaskRepository taskRepository;
    private final MissionRepository missionRepository;
    private final UserTransactionRepository userTransactionRepository;

    @Override
    public UserAnalyticsResponse getUserAnalytics(String userId) {
        long totalTasks = taskRepository.countByUserId(userId);
        long completedTasks = taskRepository.countByUserIdAndStatus(userId, "COMPLETED");
        long pendingTasks = taskRepository.countByUserIdAndStatus(userId, "PENDING");
        long completedMissions = missionRepository.countByUsersIdAndStatus(userId, "COMPLETED");
        double totalSpent = userTransactionRepository.sumTotalPriceByUserId(userId);

        return UserAnalyticsResponse.builder()
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .pendingTasks(pendingTasks)
                .completedMissions(completedMissions)
                .totalSpent(totalSpent)
                .build();
    }

    @Override
    public UserAnalyticsResponse getTaskCompletionAnalytics(String userId, String period) {
        LocalDateTime startDate = getStartDate(period);
        Map<String, Long> completionData = taskRepository.getTaskCompletionDataByUserIdAndPeriod(userId, startDate);

        return UserAnalyticsResponse.builder()
                .taskCompletionData(completionData)
                .build();
    }

    @Override
    public UserAnalyticsResponse getCompletedMissionsAnalytics(String userId) {
        Map<String, Long> missionData = missionRepository.getCompletedMissionsByUserId(userId);

        return UserAnalyticsResponse.builder()
                .completedMissionsData(missionData)
                .build();
    }

    @Override
    public UserAnalyticsResponse getBudgetAnalytics(String userId) {
        double totalSpent = userTransactionRepository.sumTotalPriceByUserId(userId);
        Map<String, Double> spendingByCategory = userTransactionRepository.getSpendingByCategoryForUser(userId);

        return UserAnalyticsResponse.builder()
                .totalSpent(totalSpent)
                .spendingByCategory(spendingByCategory)
                .build();
    }

    private LocalDateTime getStartDate(String period) {
        LocalDateTime now = LocalDateTime.now();
        switch (period.toLowerCase()) {
            case "week":
                return now.minusWeeks(1);
            case "month":
                return now.minusMonths(1);
            case "year":
                return now.minusYears(1);
            default:
                return now.minusDays(30); // Default to last 30 days
        }
    }
}