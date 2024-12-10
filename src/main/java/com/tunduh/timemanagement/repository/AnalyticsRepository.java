package com.tunduh.timemanagement.repository;

import com.tunduh.timemanagement.dto.response.OverallStats;
import com.tunduh.timemanagement.entity.TaskSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnalyticsRepository extends JpaRepository<TaskSessionEntity, String> {

    @Query("SELECT DATE(ts.endTime) as date, COUNT(DISTINCT ts.task) as taskCount, " +
            "SUM(ts.durationInSeconds) as totalDuration, SUM(t.energy) as totalEnergy " +
            "FROM TaskSessionEntity ts " +
            "JOIN ts.task t " +
            "WHERE ts.status = 'COMPLETED' AND t.user.id = :userId " +
            "AND ts.endTime BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(ts.endTime)")
    List<Object[]> getAnalyticsData(@Param("userId") String userId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT new com.tunduh.timemanagement.dto.response.OverallStats(" +
            "COUNT(DISTINCT ts.task), COALESCE(SUM(ts.durationInSeconds), 0), COALESCE(SUM(t.energy), 0)) " +
            "FROM TaskSessionEntity ts JOIN ts.task t " +
            "WHERE ts.status = 'COMPLETED' AND t.user.id = :userId")
    OverallStats getOverallStats(@Param("userId") String userId);

}
