package com.tunduh.timemanagement.repository;

import com.tunduh.timemanagement.entity.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {
    Page<TransactionEntity> findByUserId(String userId, PageRequest pageRequest);

    @Query("SELECT SUM(ut.totalPrice) FROM TransactionEntity ut WHERE ut.user.id = :userId")
    Double sumTotalPriceByUserId(@Param("userId") String userId);

    @Query("SELECT new map(si.name as category, SUM(ut.totalPrice) as totalSpent) " +
            "FROM TransactionEntity ut " +
            "JOIN ut.shopItem si " +
            "WHERE ut.user.id = :userId " +
            "GROUP BY si.name")
    List<Map<String, Double>> getSpendingByCategoryForUser(@Param("userId") String userId);

    @Query("SELECT SUM(ut.totalPrice) FROM TransactionEntity ut " +
            "WHERE ut.user.id = :userId AND ut.transactionDate BETWEEN :startDate AND :endDate")
    Double sumTotalPriceByUserIdAndDateRange(@Param("userId") String userId,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    @Query("SELECT new map(si.name as category, SUM(ut.totalPrice) as totalSpent) " +
            "FROM TransactionEntity ut " +
            "JOIN ut.shopItem si " +
            "WHERE ut.user.id = :userId AND ut.transactionDate BETWEEN :startDate AND :endDate " +
            "GROUP BY si.name")
    List<Map<String, Double>> getSpendingByCategoryForUserAndDateRange(@Param("userId") String userId,
                                                                       @Param("startDate") LocalDateTime startDate,
                                                                       @Param("endDate") LocalDateTime endDate);

    Optional<Object> findByIdAndUserId(String id, String userId);
}