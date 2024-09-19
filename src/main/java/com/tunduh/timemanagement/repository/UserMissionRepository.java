package com.tunduh.timemanagement.repository;

import com.tunduh.timemanagement.entity.UserMissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMissionRepository extends JpaRepository<UserMissionEntity, String> {

    boolean existsByMissionIdAndUserId(String missionId, String userId);

    Optional<UserMissionEntity> findByMissionIdAndUserId(String missionId, String userId);

    List<UserMissionEntity> findByUserId(String userId);

    List<UserMissionEntity> findByMissionId(String missionId);

    long countByUserIdAndIsCompleted(String userId, boolean isCompleted);

    long countByUserIdAndIsRewardClaimed(String userId, boolean isRewardClaimed);

    List<UserMissionEntity> findByUserIdAndIsCompletedAndIsRewardClaimedFalse(String userId, boolean isCompleted);
}