package com.tunduh.timemanagement.dto.response;

import com.tunduh.timemanagement.entity.TaskSessionEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskSessionResponse {
    private String id;
    private String taskId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long durationInSeconds;
    private TaskSessionEntity.SessionStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}