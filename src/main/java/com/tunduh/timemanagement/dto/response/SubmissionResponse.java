package com.tunduh.timemanagement.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class SubmissionResponse implements Serializable {
    private String id;
    private String name;
    private String description;
    private Integer point;
    private Integer criteriaCompleted;
    private String type;
    private String icon;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
