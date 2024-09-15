package com.tunduh.timemanagement.repository;

import com.tunduh.timemanagement.entity.TaskEntity;
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
}