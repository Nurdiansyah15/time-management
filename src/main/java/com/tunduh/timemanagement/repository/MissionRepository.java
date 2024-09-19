package com.tunduh.timemanagement.repository;

import com.tunduh.timemanagement.entity.MissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface MissionRepository extends JpaRepository<MissionEntity, String>, JpaSpecificationExecutor<MissionEntity> {
    long countByUsersIdAndStatus(String userId, String status);

    long countByUsersIdAndIsClaimed(String userId, boolean isClaimed);

    long countByUsersIdAndStatusAndIsRewardClaimedFalse(String userId, String status);

    List<MissionEntity> findByStatus(MissionEntity.MissionStatus status);

    List<MissionEntity> findByStartDateBeforeAndEndDateAfter(LocalDateTime now, LocalDateTime now2);

    @Query("SELECT new map(m.status as status, COUNT(m) as count) " +
            "FROM MissionEntity m JOIN m.users u " +
            "WHERE u.id = :userId " +
            "GROUP BY m.status")
    List<Map<String, Object>> getMissionDataByUserId(@Param("userId") String userId);
}