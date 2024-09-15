package com.tunduh.timemanagement.repository;

import com.tunduh.timemanagement.entity.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, String>, JpaSpecificationExecutor<TaskEntity> {
    Page<TaskEntity> findByUserId(String userId, Pageable pageable);
    TaskEntity findByIdAndUserId(String id, String userId);
}