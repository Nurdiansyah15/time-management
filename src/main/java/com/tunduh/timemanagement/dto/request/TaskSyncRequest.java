package com.tunduh.timemanagement.dto.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskSyncRequest {
    private String taskId;
    private String title;
    private String status;
    private Integer energy;
    private String notes;
    private Integer duration;
    private String priority;
    private Long version;
    private LocalDateTime lastSyncedAt;
    private List<TaskSessionSyncRequest> sessions;
}