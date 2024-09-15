package com.tunduh.timemanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
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
    @CollectionTable(name = "task_repetition_days", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "day_of_week")
    private Set<Integer> repetitionDays;

    @Column(name = "repetition_type")
    @Enumerated(EnumType.STRING)
    private RepetitionType repetitionType;

    @Column(name = "repetition_end_date")
    private LocalDateTime repetitionEndDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum RepetitionType {
        NONE, DAILY, WEEKLY, MONTHLY, YEARLY
    }
}