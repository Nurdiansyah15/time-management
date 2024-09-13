package com.tunduh.timemanagement.repository;

import com.tunduh.timemanagement.entity.UserTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTransactionRepository extends JpaRepository<UserTransactionEntity, String> {
    List<UserTransactionEntity> findByUserId(String userId);
    Optional<UserTransactionEntity> findByIdAndUserId(String id, String userId);
}