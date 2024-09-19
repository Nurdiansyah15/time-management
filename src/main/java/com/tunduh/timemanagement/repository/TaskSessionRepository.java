package com.tunduh.timemanagement.repository;

import com.tunduh.timemanagement.entity.TaskSessionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskSessionRepository extends JpaRepository<TaskSessionEntity, String>, JpaSpecificationExecutor<TaskSessionEntity> {
    Page<TaskSessionEntity> findByTaskId(String taskId, Pageable pageable);
    Optional<TaskSessionEntity> findByIdAndTaskUserId(String sessionId, String userId);
}