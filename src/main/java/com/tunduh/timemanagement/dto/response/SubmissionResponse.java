package com.tunduh.timemanagement.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class SubmissionResponse implements Serializable {
    private String id;
    private String title;
    private String description;
    private String status;
    private String submissionPicture;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}