package com.tunduh.timemanagement.service.impl;

import com.tunduh.timemanagement.dto.response.UserAnalyticsResponse;
import com.tunduh.timemanagement.entity.MissionEntity;
import com.tunduh.timemanagement.repository.MissionRepository;
import com.tunduh.timemanagement.repository.TaskRepository;
import com.tunduh.timemanagement.repository.TransactionRepository;
import com.tunduh.timemanagement.service.UserAnalyticsService;
import com.tunduh.timemanagement.utils.CSVUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAnalyticsServiceImpl implements UserAnalyticsService {

    private static final String COMPLETED_STATUS = "COMPLETED";
    private static final String PENDING_STATUS = "PENDING";

    private final TaskRepository taskRepository;
    private final MissionRepository missionRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public UserAnalyticsResponse getUserAnalyticsDashboard(String userId) {
        return UserAnalyticsResponse.builder()
                .totalTasks(taskRepository.countByUserId(userId))
                .completedTasks(taskRepository.countByUserIdAndStatus(userId, COMPLETED_STATUS))
                .pendingTasks(taskRepository.countByUserIdAndStatus(userId, PENDING_STATUS))
                .completedMissions(missionRepository.countByUserIdAndIsCompleted(userId, true))
                .claimedMissions(missionRepository.countByUserIdAndIsCompleted(userId, true))
                .totalPointsChange(transactionRepository.sumPointsChangeByUserId(userId))
                .unclaimedMissionRewards(missionRepository.countByUserIdAndStatusAndIsRewardClaimedFalse(userId, MissionEntity.MissionStatus.COMPLETED))
                .build();
    }

    @Override
    public UserAnalyticsResponse getTaskAnalytics(String userId, LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> taskData = getTaskDataForDateRange(userId, startDate, endDate);
        return UserAnalyticsResponse.builder()
                .taskCompletionByDate(getGroupedData(taskData, "date", "count"))
                .taskStatusCounts(getGroupedData(taskData, "status", "count"))
                .build();
    }

    @Override
    public UserAnalyticsResponse getMissionAnalytics(String userId) {
        List<Map<String, Object>> missionData = missionRepository.getMissionDataByUserId(userId);
        return UserAnalyticsResponse.builder()
                .missionStatusCounts(getGroupedData(missionData, "status", "count"))
                .build();
    }

    @Override
    public UserAnalyticsResponse getBudgetAnalytics(String userId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        Double totalPointsChange = transactionRepository.sumPointsChangeByUserIdAndDateRange(userId, start, end);
        List<Map<String, Object>> transactionSummary = transactionRepository.getTransactionSummaryByUserIdAndDateRange(userId, start, end);

        return UserAnalyticsResponse.builder()
                .totalPointsChange(totalPointsChange != null ? totalPointsChange : 0.0)
                .pointsChangeByCategory(getGroupedData(transactionSummary, "category", "totalChange"))
                .build();
    }

    @Override
    public String getTaskAnalyticsCSV(String userId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating task analytics CSV for user {} from {} to {}", userId, startDate, endDate);
        List<Map<String, Object>> taskData = getTaskDataForDateRange(userId, startDate, endDate);
        return generateCSV(taskData, new String[]{"Date", "Status", "Count"},
                entry -> new String[]{entry.get("date").toString(), entry.get("status").toString(), entry.get("count").toString()});
    }

    @Override
    public String getMissionAnalyticsCSV(String userId) {
        log.info("Generating mission analytics CSV for user {}", userId);
        List<Map<String, Object>> missionData = missionRepository.getMissionDataByUserId(userId);
        return generateCSV(missionData, new String[]{"Status", "Count"},
                entry -> new String[]{entry.get("status").toString(), entry.get("count").toString()});
    }

    @Override
    public String getBudgetAnalyticsCSV(String userId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating budget analytics CSV for user {} from {} to {}", userId, startDate, endDate);
        List<Map<String, Object>> transactionSummary = getTransactionSummaryForDateRange(userId, startDate, endDate);
        return generateCSV(transactionSummary, new String[]{"Category", "Total Points Change"},
                entry -> new String[]{entry.get("category").toString(), entry.get("totalChange").toString()});
    }

    private List<Map<String, Object>> getTaskDataForDateRange(String userId, LocalDate startDate, LocalDate endDate) {
        return taskRepository.getTaskDataByUserIdAndDateRange(userId, startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
    }

    private List<Map<String, Object>> getTransactionSummaryForDateRange(String userId, LocalDate startDate, LocalDate endDate) {
        return transactionRepository.getTransactionSummaryByUserIdAndDateRange(userId, startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
    }

    private <T> Map<String, T> getGroupedData(List<Map<String, Object>> data, String keyField, String valueField) {
        return data.stream()
                .collect(Collectors.groupingBy(
                        m -> m.get(keyField).toString(),
                        Collectors.summingDouble(m -> ((Number) m.get(valueField)).doubleValue())
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> {
                            Object value = e.getValue();
                            if (valueField.equals("count")) {
                                return (T) Long.valueOf(((Number) value).longValue());
                            } else {
                                return (T) value;
                            }
                        }
                ));
    }

    private String generateCSV(List<Map<String, Object>> data, String[] headers, java.util.function.Function<Map<String, Object>, String[]> rowMapper) {
        List<String[]> csvData = new ArrayList<>();
        csvData.add(headers);
        data.forEach(entry -> csvData.add(rowMapper.apply(entry)));
        return CSVUtil.generateCSV(csvData);
    }
}