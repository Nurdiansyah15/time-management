package com.tunduh.timemanagement.repository;

import com.tunduh.timemanagement.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String>, JpaSpecificationExecutor<UserEntity> {
    Optional<UserEntity> findByEmail(String email);
    Boolean existsByEmail(String email);
    Optional<UserEntity> findByUsername(String username);

    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.lastLoginDate >= CURRENT_DATE - 30")
    long countActiveUsers();

    @Query("SELECT AVG(SIZE(u.tasks)) FROM UserEntity u")
    double getAverageTasksPerUser();
}