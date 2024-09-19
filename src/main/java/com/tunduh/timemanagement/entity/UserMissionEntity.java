package com.tunduh.timemanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_missions")
public class UserMissionEntity {
    @Column(nullable = false)
    private final Boolean isCompleted = false;
    @Column(nullable = false)
    private final Boolean isRewardClaimed = false;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    @ManyToOne
    @JoinColumn(name = "mission_id")
    private MissionEntity mission;
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "reward_claimed_at")
    private LocalDateTime rewardClaimedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}