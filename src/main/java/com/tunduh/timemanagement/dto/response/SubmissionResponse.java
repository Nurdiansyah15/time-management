package com.tunduh.timemanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionResponse {
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