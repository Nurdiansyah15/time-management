package com.tunduh.timemanagement.dto.response;

import com.tunduh.timemanagement.entity.TaskSessionEntity;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class TaskSessionResponse implements Serializable {
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