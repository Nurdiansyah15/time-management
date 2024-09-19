package com.tunduh.timemanagement.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class DailyTaskAnalyticsResponse implements Serializable {
    private LocalDate date;
    private List<Map<String, Object>> taskData;
}
