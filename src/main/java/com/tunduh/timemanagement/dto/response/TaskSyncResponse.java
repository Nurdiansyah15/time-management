package com.tunduh.timemanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskSyncResponse {
    private String taskId;
    private String title;
    private Integer energy;
    private String notes;
    private String status;
    private Integer duration;
    private String priority;
    private LocalDateTime lastSyncedAt;
    private Long version;
    private boolean conflicted;
}