package com.tunduh.timemanagement.service.impl;

import com.tunduh.timemanagement.dto.response.UserAnalyticsResponse;
import com.tunduh.timemanagement.repository.MissionRepository;
import com.tunduh.timemanagement.repository.TaskRepository;
import com.tunduh.timemanagement.repository.TransactionRepository;
import com.tunduh.timemanagement.service.UserAnalyticsService;
import com.tunduh.timemanagement.utils.CSVUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAnalyticsServiceImpl implements UserAnalyticsService {

    private final TaskRepository taskRepository;
    private final MissionRepository missionRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public UserAnalyticsResponse getUserAnalyticsDashboard(String userId) {
        long totalTasks = taskRepository.countByUserId(userId);
        long completedTasks = taskRepository.countByUserIdAndStatus(userId, "COMPLETED");
        long pendingTasks = taskRepository.countByUserIdAndStatus(userId, "PENDING");
        long completedMissions = missionRepository.countByUsersIdAndStatus(userId, "COMPLETED");
        Double totalPointsChange = transactionRepository.sumPointsChangeByUserId(userId);

        return UserAnalyticsResponse.builder()
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .pendingTasks(pendingTasks)
                .completedMissions(completedMissions)
                .totalPointsChange(totalPointsChange != null ? totalPointsChange : 0.0)
                .build();
    }

    @Override
    public UserAnalyticsResponse getTaskAnalytics(String userId, LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> taskData = taskRepository.getTaskDataByUserIdAndDateRange(userId,
                startDate.atStartOfDay(), endDate.atTime(23, 59, 59));

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
        Double totalPointsChange = transactionRepository.sumPointsChangeByUserIdAndDateRange(userId,
                startDate.atStartOfDay(), endDate.atTime(23, 59, 59));

        List<Map<String, Object>> transactionSummary = transactionRepository.getTransactionSummaryByUserIdAndDateRange(userId,
                startDate.atStartOfDay(), endDate.atTime(23, 59, 59));

        Map<String, Double> pointsChangeByCategory = transactionSummary.stream()
                .collect(Collectors.toMap(
                        m -> m.get("category").toString(),
                        m -> ((Number) m.get("totalChange")).doubleValue()
                ));

        return UserAnalyticsResponse.builder()
                .totalPointsChange(totalPointsChange != null ? totalPointsChange : 0.0)
                .pointsChangeByCategory(pointsChangeByCategory)
                .build();
    }

    @Override
    public String getTaskAnalyticsCSV(String userId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating task analytics CSV for user {} from {} to {}", userId, startDate, endDate);
        List<Map<String, Object>> taskData = taskRepository.getTaskDataByUserIdAndDateRange(userId,
                startDate.atStartOfDay(), endDate.atTime(23, 59, 59));

        List<String[]> csvData = new ArrayList<>();
        csvData.add(new String[]{"Date", "Status", "Count"});
        for (Map<String, Object> entry : taskData) {
            csvData.add(new String[]{
                    entry.get("date").toString(),
                    entry.get("status").toString(),
                    entry.get("count").toString()
            });
        }
        return CSVUtil.generateCSV(csvData);
    }

    @Override
    public String getMissionAnalyticsCSV(String userId) {
        log.info("Generating mission analytics CSV for user {}", userId);
        List<Map<String, Object>> missionData = missionRepository.getMissionDataByUserId(userId);

        List<String[]> csvData = new ArrayList<>();
        csvData.add(new String[]{"Status", "Count"});

        for (Map<String, Object> entry : missionData) {
            csvData.add(new String[]{
                    entry.get("status").toString(),
                    entry.get("count").toString()
            });
        }

        return CSVUtil.generateCSV(csvData);
    }

    @Override
    public String getBudgetAnalyticsCSV(String userId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating budget analytics CSV for user {} from {} to {}", userId, startDate, endDate);
        List<Map<String, Object>> transactionSummary = transactionRepository.getTransactionSummaryByUserIdAndDateRange(userId,
                startDate.atStartOfDay(), endDate.atTime(23, 59, 59));

        List<String[]> csvData = new ArrayList<>();
        csvData.add(new String[]{"Category", "Total Points Change"});
        for (Map<String, Object> entry : transactionSummary) {
            csvData.add(new String[]{
                    entry.get("category").toString(),
                    entry.get("totalChange").toString()
            });
        }
        return CSVUtil.generateCSV(csvData);
    }
}