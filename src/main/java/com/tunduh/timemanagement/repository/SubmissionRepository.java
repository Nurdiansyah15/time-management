package com.tunduh.timemanagement.repository;

import com.tunduh.timemanagement.entity.SubmissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionRepository extends JpaRepository<SubmissionEntity, String>, JpaSpecificationExecutor<SubmissionEntity> {
}