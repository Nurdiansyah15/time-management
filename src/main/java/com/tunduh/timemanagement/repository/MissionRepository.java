package com.tunduh.timemanagement.repository;

import com.tunduh.timemanagement.entity.MissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MissionRepository extends JpaRepository<MissionEntity, String> {
    List<MissionEntity> findByUsersId(String userId);
    Optional<MissionEntity> findByIdAndUsersId(String id, String userId);
}