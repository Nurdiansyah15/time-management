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
import java.util.Optional;

@Repository
public interface MissionRepository extends JpaRepository<MissionEntity, String>, JpaSpecificationExecutor<MissionEntity> {
    Optional<MissionEntity> findByIdAndUsersId(String id, String userId);
    long countByUsersIdAndStatus(String userId, String status);
    List<MissionEntity> findByEndDateAfter(LocalDateTime now);
    List<MissionEntity> findByUsersIdAndEndDateAfter(String userId, LocalDateTime now);
    List<MissionEntity> findByEndDateBefore(LocalDateTime now);
    List<MissionEntity> findByStatus(String status);

    @Query("SELECT new map(m.status as status, COUNT(m) as count) " +
            "FROM MissionEntity m JOIN m.users u " +
            "WHERE u.id = :userId " +
            "GROUP BY m.status")
    List<Map<String, Object>> getMissionDataByUserId(@Param("userId") String userId);

    @Query("SELECT m FROM MissionEntity m WHERE m.startDate <= :now AND m.endDate >= :now AND m.status = 'ACTIVE' AND m NOT IN (SELECT um.mission FROM UserMissionEntity um WHERE um.user.id = :userId)")
    List<MissionEntity> findAvailableMissionsForUser(@Param("userId") String userId, @Param("now") LocalDateTime now);

    long countByUsersIdAndStatusAndIsRewardClaimedFalse(String userId, String status);
    long countByUsersIdAndIsClaimed(String userId, boolean isClaimed);
}