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

    @Query("SELECT COUNT(um) FROM UserMissionEntity um WHERE um.user.id = :userId AND um.mission.status = :status")
    long countByUserIdAndStatus(@Param("userId") String userId, @Param("status") MissionEntity.MissionStatus status);

    @Query("SELECT COUNT(um) FROM UserMissionEntity um WHERE um.user.id = :userId AND um.isCompleted = :isCompleted")
    long countByUserIdAndIsCompleted(@Param("userId") String userId, @Param("isCompleted") boolean isCompleted);

    @Query("SELECT COUNT(um) FROM UserMissionEntity um WHERE um.user.id = :userId AND um.mission.status = :status AND um.isRewardClaimed = false")
    long countByUserIdAndStatusAndIsRewardClaimedFalse(@Param("userId") String userId, @Param("status") MissionEntity.MissionStatus status);

    List<MissionEntity> findByStatus(MissionEntity.MissionStatus status);

    List<MissionEntity> findByStartDateBeforeAndEndDateAfter(LocalDateTime now, LocalDateTime now2);

    @Query("SELECT new map(m.status as status, COUNT(um) as count) " +
            "FROM MissionEntity m JOIN m.userMissions um " +
            "WHERE um.user.id = :userId " +
            "GROUP BY m.status")
    List<Map<String, Object>> getMissionDataByUserId(@Param("userId") String userId);
}