package com.tunduh.timemanagement.repository;

import com.tunduh.timemanagement.entity.MissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface MissionRepository extends JpaRepository<MissionEntity, String>, JpaSpecificationExecutor<MissionEntity> {
    Optional<MissionEntity> findByIdAndUsersId(String id, String userId);
    long countByUsersIdAndStatus(String userId, String status);

    @Query("SELECT new map(m.status as status, COUNT(m) as count) " +
            "FROM MissionEntity m JOIN m.users u " +
            "WHERE u.id = :userId " +
            "GROUP BY m.status")
    List<Map<String, Object>> getCompletedMissionsByUserId(@Param("userId") String userId);

    @Query("SELECT new map(m.status as status, COUNT(m) as count) " +
            "FROM MissionEntity m JOIN m.users u " +
            "WHERE u.id = :userId " +
            "GROUP BY m.status")
    List<Map<String, Object>> getMissionDataByUserId(@Param("userId") String userId);
}