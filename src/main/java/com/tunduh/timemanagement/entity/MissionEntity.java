package com.tunduh.timemanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "missions")
public class MissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Integer pointReward;

    @Column(name = "mission_picture")
    private String missionPicture;

    @ManyToOne
    @JoinColumn(name = "parent_mission_id")
    private MissionEntity parentMission;

    @OneToMany(mappedBy = "parentMission", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MissionEntity> subMissions = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_missions",
            joinColumns = @JoinColumn(name = "mission_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity> users = new HashSet<>();

    @Column(nullable = false)
    private Integer requiredTaskCount;

    @Column(nullable = false)
    private Integer requiredDuration;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}