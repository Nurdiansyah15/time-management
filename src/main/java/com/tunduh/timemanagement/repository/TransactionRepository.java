package com.tunduh.timemanagement.repository;

import com.tunduh.timemanagement.entity.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {
    Page<TransactionEntity> findByUserId(String userId, Pageable pageable);

    @Query("SELECT SUM(t.pointsChange) FROM TransactionEntity t WHERE t.user.id = :userId")
    Optional<Double> sumPointsChangeByUserId(@Param("userId") String userId);

    @Query("SELECT new map(t.type as category, SUM(t.pointsChange) as totalChange) " +
            "FROM TransactionEntity t " +
            "WHERE t.user.id = :userId " +
            "GROUP BY t.type")
    List<Map<String, Object>> getTransactionSummaryByUserId(@Param("userId") String userId);

    @Query("SELECT SUM(t.pointsChange) FROM TransactionEntity t " +
            "WHERE t.user.id = :userId AND t.createdAt BETWEEN :startDate AND :endDate")
    Double sumPointsChangeByUserIdAndDateRange(@Param("userId") String userId,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);

    @Query("SELECT new map(t.type as category, SUM(t.pointsChange) as totalChange) " +
            "FROM TransactionEntity t " +
            "WHERE t.user.id = :userId AND t.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY t.type")
    List<Map<String, Object>> getTransactionSummaryByUserIdAndDateRange(@Param("userId") String userId,
                                                                        @Param("startDate") LocalDateTime startDate,
                                                                        @Param("endDate") LocalDateTime endDate);

    Optional<TransactionEntity> findByIdAndUserId(String id, String userId);
}