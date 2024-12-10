package com.tunduh.timemanagement.repository;

import com.tunduh.timemanagement.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String>, JpaSpecificationExecutor<UserEntity> {
    Optional<UserEntity> findByEmail(String email);
    Boolean existsByEmail(String email);
    Optional<UserEntity> findByUsername(String username);

    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.lastLoginDate >= :thirtyDaysAgo")
    long countActiveUsers(LocalDate thirtyDaysAgo);

//    @Query("SELECT AVG(SIZE(u.tasks)) FROM UserEntity u")
//    double getAverageTasksPerUser();

    List<UserEntity> findByResetTimeLessThanEqualAndLastResetDateBefore(LocalTime now, LocalDate today);
}