package com.tunduh.timemanagement.repository;

import com.tunduh.timemanagement.entity.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, String>, JpaSpecificationExecutor<TaskEntity> {
    Page<TaskEntity> findByUserId(String userId, Pageable pageable);
    TaskEntity findByIdAndUserId(String id, String userId);
    long countByUserId(String userId);
    long countByUserIdAndStatus(String userId, String status);

    @Query("SELECT new map(DATE(t.createdAt) as date, COUNT(t) as count) " +
            "FROM TaskEntity t " +
            "WHERE t.user.id = :userId AND t.createdAt >= :startDate " +
            "GROUP BY DATE(t.createdAt)")
    List<Map<String, Object>> getTaskCompletionDataByUserIdAndPeriod(@Param("userId") String userId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT new map(t.status as status, COUNT(t) as count, SUM(t.duration) as totalDuration) " +
            "FROM TaskEntity t " +
            "WHERE t.user.id = :userId AND DATE(t.createdAt) = :date " +
            "GROUP BY t.status")
    List<Map<String, Object>> getDailyTaskDataForUser(@Param("userId") String userId, @Param("date") LocalDate date);

    @Query("SELECT new map(t.status as status, SUM(t.energy) as totalEnergy) " +
            "FROM TaskEntity t " +
            "WHERE t.user.id = :userId " +
            "GROUP BY t.status")
    List<Map<String, Double>> getBudgetSpentOnTasksForUser(@Param("userId") String userId);

    @Query("SELECT t FROM TaskEntity t " +
            "WHERE t.repetitionType != com.tunduh.timemanagement.entity.TaskEntity.RepetitionType.NONE " +
            "AND (t.repetitionEndDate IS NULL OR t.repetitionEndDate >= :currentDate)")
    List<TaskEntity> findAllActiveRecurringTasks(@Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT t FROM TaskEntity t " +
            "WHERE t.repetitionType = com.tunduh.timemanagement.entity.TaskEntity.RepetitionType.DAILY " +
            "AND (t.repetitionEndDate IS NULL OR t.repetitionEndDate >= :currentDate)")
    List<TaskEntity> findDailyRecurringTasks(@Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT t FROM TaskEntity t " +
            "WHERE t.repetitionType = com.tunduh.timemanagement.entity.TaskEntity.RepetitionType.WEEKLY " +
            "AND :dayOfWeek MEMBER OF t.repetitionDays " +
            "AND (t.repetitionEndDate IS NULL OR t.repetitionEndDate >= :currentDate)")
    List<TaskEntity> findWeeklyRecurringTasks(@Param("currentDate") LocalDateTime currentDate, @Param("dayOfWeek") int dayOfWeek);

    @Query("SELECT t FROM TaskEntity t " +
            "WHERE t.repetitionType = com.tunduh.timemanagement.entity.TaskEntity.RepetitionType.MONTHLY " +
            "AND DAY(t.createdAt) = DAY(:currentDate) " +
            "AND (t.repetitionEndDate IS NULL OR t.repetitionEndDate >= :currentDate)")
    List<TaskEntity> findMonthlyRecurringTasks(@Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT t FROM TaskEntity t " +
            "WHERE t.repetitionType = com.tunduh.timemanagement.entity.TaskEntity.RepetitionType.YEARLY " +
            "AND DAY(t.createdAt) = DAY(:currentDate) " +
            "AND MONTH(t.createdAt) = MONTH(:currentDate) " +
            "AND (t.repetitionEndDate IS NULL OR t.repetitionEndDate >= :currentDate)")
    List<TaskEntity> findYearlyRecurringTasks(@Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT new map(DATE(t.createdAt) as date, t.status as status, COUNT(t) as count) " +
            "FROM TaskEntity t " +
            "WHERE t.user.id = :userId AND t.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(t.createdAt), t.status")
    List<Map<String, Object>> getTaskDataByUserIdAndDateRange(@Param("userId") String userId,
                                                              @Param("startDate") LocalDateTime startDate,
                                                              @Param("endDate") LocalDateTime endDate);
}