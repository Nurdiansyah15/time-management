package com.tunduh.timemanagement.repository;

import com.tunduh.timemanagement.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, String> {

    List<TaskEntity> findByUserId(String user_id);

    TaskEntity findByIdAndUserId(String id, String user_id);
}