package com.tunduh.timemanagement.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
public class BudgetAnalyticsResponse implements Serializable {
    private double totalSpent;
    private Map<String, Double> spendingByCategory;
    private Map<String, Double> budgetForTasks;
}
