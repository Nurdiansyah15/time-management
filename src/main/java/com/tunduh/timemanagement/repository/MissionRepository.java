package com.tunduh.timemanagement.repository;

import com.tunduh.timemanagement.entity.MissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MissionRepository extends JpaRepository<MissionEntity, String>, JpaSpecificationExecutor<MissionEntity> {
    List<MissionEntity> findByStatus(MissionEntity.MissionStatus status);
    List<MissionEntity> findByStartDateBeforeAndEndDateAfter(LocalDateTime now, LocalDateTime now2);
}