package com.tunduh.timemanagement.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskSessionSyncRequest {
    private String sessionId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long durationInSeconds;
    private String status;
    private String notes;
    private Long version;
}