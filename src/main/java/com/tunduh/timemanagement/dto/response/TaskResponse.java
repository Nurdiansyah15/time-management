package com.tunduh.timemanagement.dto.response;

import com.tunduh.timemanagement.entity.TaskEntity.RepetitionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String title;
    private Integer energy;
    private String notes;
    private String status;
    private Integer duration;
    private String priority;
    private RepetitionType repetitionType;
    private Set<Integer> repetitionDates;
    private LocalDateTime repetitionStartDate;
    private LocalDateTime repetitionEndDate;
    private Integer repetitionInterval;
    private String taskPicture;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}