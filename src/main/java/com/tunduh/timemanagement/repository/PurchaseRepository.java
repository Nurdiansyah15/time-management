package com.tunduh.timemanagement.repository;

import com.tunduh.timemanagement.entity.PurchaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<PurchaseEntity, String> {
    Page<PurchaseEntity> findByUserId(String userId, Pageable pageable);
    Optional<PurchaseEntity> findByIdAndUserId(String id, String userId);
}