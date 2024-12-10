package com.tunduh.timemanagement.entity;

import com.tunduh.timemanagement.validation.EnumValidator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tasks")
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer energy;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false)
    private String priority;

    @ElementCollection
    @CollectionTable(name = "task_repetition_dates", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "date")
    private Set<Integer> repetitionDates;

    @EnumValidator(enumClass = TaskEntity.RepetitionType.class, message = "Invalid repetition type")
    @Column(name = "repetition_type")
    @Enumerated(EnumType.STRING)
    private RepetitionType repetitionType;

    @Column(name = "repetition_start_date")
    private LocalDateTime repetitionStartDate;

    @Column(name = "repetition_end_date")
    private LocalDateTime repetitionEndDate;

    @Column(name = "repetition_interval")
    private Integer repetitionInterval;

    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TaskSessionEntity> sessions = new HashSet<>();

    private Long version;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "task_picture")
    private String taskPicture;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum RepetitionType {
        NONE, DAILY, WEEKLY, MONTHLY,YEARLY,CUSTOM, RANGE, LIFETIME
    }
}