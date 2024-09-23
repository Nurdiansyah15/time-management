package com.tunduh.timemanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OverallStats {
    private Long totalTasksCompleted;
    private Long totalDuration;
    private Long totalEnergy;
}
