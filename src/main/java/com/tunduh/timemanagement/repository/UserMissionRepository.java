package com.tunduh.timemanagement.repository;

import com.tunduh.timemanagement.entity.UserMissionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMissionRepository extends JpaRepository<UserMissionEntity, String> {

    boolean existsByMissionIdAndUserId(String missionId, String userId);

    Optional<UserMissionEntity> findByMissionIdAndUserId(String missionId, String userId);

    List<UserMissionEntity> findByUserId(String userId);

    Page<UserMissionEntity> findByUserId(String userId, Pageable pageable);

    Page<UserMissionEntity> findByUserIdAndIsCompleted(String userId, boolean b, Pageable pageable);
}