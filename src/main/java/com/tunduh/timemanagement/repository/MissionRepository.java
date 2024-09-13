package com.tunduh.timemanagement.repository;

import com.tunduh.timemanagement.dto.response.MissionResponse;
import com.tunduh.timemanagement.entity.MissionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MissionRepository extends JpaRepository<MissionEntity, String>, JpaSpecificationExecutor<MissionEntity> {
    Page<MissionEntity> findByUsersId(String userId, Pageable pageable);
    Optional<MissionEntity> findByIdAndUsersId(String id, String userId);
}