package com.tunduh.timemanagement.repository;

import com.tunduh.timemanagement.entity.UserTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface UserTransactionRepository extends JpaRepository<UserTransactionEntity, String> {
    List<UserTransactionEntity> findByUserId(String userId);

    @Query("SELECT SUM(ut.totalPrice) FROM UserTransactionEntity ut WHERE ut.user.id = :userId")
    Double sumTotalPriceByUserId(@Param("userId") String userId);

    @Query("SELECT new map(si.name as category, SUM(ut.totalPrice) as totalSpent) " +
            "FROM UserTransactionEntity ut " +
            "JOIN ut.shopItem si " +
            "WHERE ut.user.id = :userId " +
            "GROUP BY si.name")
    List<Map<String, Double>> getSpendingByCategoryForUser(@Param("userId") String userId);

    @Query("SELECT SUM(ut.totalPrice) FROM UserTransactionEntity ut " +
            "WHERE ut.user.id = :userId AND ut.transactionDate BETWEEN :startDate AND :endDate")
    Double sumTotalPriceByUserIdAndDateRange(@Param("userId") String userId,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    @Query("SELECT new map(si.name as category, SUM(ut.totalPrice) as totalSpent) " +
            "FROM UserTransactionEntity ut " +
            "JOIN ut.shopItem si " +
            "WHERE ut.user.id = :userId AND ut.transactionDate BETWEEN :startDate AND :endDate " +
            "GROUP BY si.name")
    List<Map<String, Double>> getSpendingByCategoryForUserAndDateRange(@Param("userId") String userId,
                                                                       @Param("startDate") LocalDateTime startDate,
                                                                       @Param("endDate") LocalDateTime endDate);
}