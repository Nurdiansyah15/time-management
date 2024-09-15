package com.tunduh.timemanagement.service.impl;

import com.tunduh.timemanagement.dto.response.UserAnalyticsResponse;
import com.tunduh.timemanagement.repository.TaskRepository;
import com.tunduh.timemanagement.repository.MissionRepository;
import com.tunduh.timemanagement.repository.UserTransactionRepository;
import com.tunduh.timemanagement.service.UserAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAnalyticsServiceImpl implements UserAnalyticsService {

    private final TaskRepository taskRepository;
    private final MissionRepository missionRepository;
    private final UserTransactionRepository userTransactionRepository;

    @Override
    public UserAnalyticsResponse getUserAnalyticsDashboard(String userId) {
        long totalTasks = taskRepository.countByUserId(userId);
        long completedTasks = taskRepository.countByUserIdAndStatus(userId, "COMPLETED");
        long pendingTasks = taskRepository.countByUserIdAndStatus(userId, "PENDING");
        long completedMissions = missionRepository.countByUsersIdAndStatus(userId, "COMPLETED");
        Double totalSpent = userTransactionRepository.sumTotalPriceByUserId(userId);

        return UserAnalyticsResponse.builder()
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .pendingTasks(pendingTasks)
                .completedMissions(completedMissions)
                .totalSpent(totalSpent != null ? totalSpent : 0.0)
                .build();
    }

    @Override
    public UserAnalyticsResponse getTaskAnalytics(String userId, LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> taskData = taskRepository.getTaskDataByUserIdAndDateRange(userId, startDate.atStartOfDay(), endDate.atTime(23, 59, 59));

        Map<String, Long> taskCompletionByDate = taskData.stream()
                .collect(Collectors.groupingBy(
                        m -> m.get("date").toString(),
                        Collectors.summingLong(m -> ((Number) m.get("count")).longValue())
                ));

        Map<String, Long> taskStatusCounts = taskData.stream()
                .collect(Collectors.groupingBy(
                        m -> m.get("status").toString(),
                        Collectors.summingLong(m -> ((Number) m.get("count")).longValue())
                ));

        return UserAnalyticsResponse.builder()
                .taskCompletionByDate(taskCompletionByDate)
                .taskStatusCounts(taskStatusCounts)
                .build();
    }

    @Override
    public UserAnalyticsResponse getMissionAnalytics(String userId) {
        List<Map<String, Object>> missionData = missionRepository.getMissionDataByUserId(userId);

        Map<String, Long> missionStatusCounts = missionData.stream()
                .collect(Collectors.groupingBy(
                        m -> m.get("status").toString(),
                        Collectors.summingLong(m -> ((Number) m.get("count")).longValue())
                ));

        return UserAnalyticsResponse.builder()
                .missionStatusCounts(missionStatusCounts)
                .build();
    }

    @Override
    public UserAnalyticsResponse getBudgetAnalytics(String userId, LocalDate startDate, LocalDate endDate) {
        Double totalSpent = userTransactionRepository.sumTotalPriceByUserIdAndDateRange(userId, startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        List<Map<String, Double>> spendingByCategoryList = userTransactionRepository.getSpendingByCategoryForUserAndDateRange(userId, startDate.atStartOfDay(), endDate.atTime(23, 59, 59));

        Map<String, Double> spendingByCategory = spendingByCategoryList.stream()
                .collect(Collectors.toMap(
                        m -> m.get("category").toString(),
                        m -> m.get("totalSpent")
                ));

        return UserAnalyticsResponse.builder()
                .totalSpent(totalSpent != null ? totalSpent : 0.0)
                .spendingByCategory(spendingByCategory)
                .build();
    }
}