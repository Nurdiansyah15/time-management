package com.tunduh.timemanagement.service.impl;

import com.tunduh.timemanagement.dto.response.UserAnalyticsResponse;
import com.tunduh.timemanagement.dto.response.BudgetAnalyticsResponse;
import com.tunduh.timemanagement.dto.response.DailyTaskAnalyticsResponse;
import com.tunduh.timemanagement.repository.TaskRepository;
import com.tunduh.timemanagement.repository.MissionRepository;
import com.tunduh.timemanagement.repository.UserTransactionRepository;
import com.tunduh.timemanagement.service.UserAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    public UserAnalyticsResponse getUserAnalytics(String userId) {
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
    public UserAnalyticsResponse getTaskCompletionAnalytics(String userId, String period) {
        LocalDateTime startDate = getStartDate(period);
        List<Map<String, Object>> completionData = taskRepository.getTaskCompletionDataByUserIdAndPeriod(userId, startDate);

        Map<String, Long> formattedCompletionData = completionData.stream()
                .collect(Collectors.toMap(
                        m -> m.get("date").toString(),
                        m -> (Long) m.get("count")
                ));

        return UserAnalyticsResponse.builder()
                .taskCompletionData(formattedCompletionData)
                .build();
    }

    @Override
    public UserAnalyticsResponse getCompletedMissionsAnalytics(String userId) {
        // Implement this method based on your requirements
        return null;
    }

    @Override
    public BudgetAnalyticsResponse getBudgetAnalytics(String userId) {
        Double totalSpent = userTransactionRepository.sumTotalPriceByUserId(userId);
        List<Map<String, Double>> spendingByCategoryList = userTransactionRepository.getSpendingByCategoryForUser(userId);
        Map<String, Double> spendingByCategory = spendingByCategoryList.stream()
                .collect(Collectors.toMap(
                        m -> (String) m.get("category"),
                        m -> m.get("totalSpent")
                ));

        List<Map<String, Double>> budgetForTasksList = taskRepository.getBudgetSpentOnTasksForUser(userId);
        Map<String, Double> budgetForTasks = budgetForTasksList.stream()
                .collect(Collectors.toMap(
                        m -> (String) m.get("status"),
                        m -> m.get("totalEnergy")
                ));

        return BudgetAnalyticsResponse.builder()
                .totalSpent(totalSpent != null ? totalSpent : 0.0)
                .spendingByCategory(spendingByCategory)
                .budgetForTasks(budgetForTasks)
                .build();
    }

    @Override
    public DailyTaskAnalyticsResponse getDailyTaskAnalytics(String userId, LocalDate date) {
        List<Map<String, Object>> taskData = taskRepository.getDailyTaskDataForUser(userId, date);

        return DailyTaskAnalyticsResponse.builder()
                .date(date)
                .taskData(taskData)
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