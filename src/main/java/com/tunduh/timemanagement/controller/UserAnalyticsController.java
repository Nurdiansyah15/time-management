package com.tunduh.timemanagement.controller;

import com.tunduh.timemanagement.dto.response.BudgetAnalyticsResponse;
import com.tunduh.timemanagement.dto.response.DailyTaskAnalyticsResponse;
import com.tunduh.timemanagement.dto.response.UserAnalyticsResponse;
import com.tunduh.timemanagement.service.UserAnalyticsService;
import com.tunduh.timemanagement.utils.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/user/analytics")
@RequiredArgsConstructor
@Tag(name = "User Analytics", description = "User analytics operations")
public class UserAnalyticsController {

    private final UserAnalyticsService userAnalyticsService;

    @GetMapping
    @Operation(summary = "Get user analytics")
    public ResponseEntity<?> getUserAnalytics(Authentication authentication) {
        String userId = authentication.getName();
        UserAnalyticsResponse analytics = userAnalyticsService.getUserAnalytics(userId);
        return Response.renderJSON(analytics);
    }

    @GetMapping("/tasks/completion")
    @Operation(summary = "Get task completion analytics")
    public ResponseEntity<?> getTaskCompletionAnalytics(
            Authentication authentication,
            @RequestParam(required = false) String period) {
        String userId = authentication.getName();
        UserAnalyticsResponse analytics = userAnalyticsService.getTaskCompletionAnalytics(userId, period);
        return Response.renderJSON(analytics);
    }

    @GetMapping("/missions/completed")
    @Operation(summary = "Get completed missions analytics")
    public ResponseEntity<?> getCompletedMissionsAnalytics(Authentication authentication) {
        String userId = authentication.getName();
        UserAnalyticsResponse analytics = userAnalyticsService.getCompletedMissionsAnalytics(userId);
        return Response.renderJSON(analytics);
    }

    @GetMapping("/budget")
    @Operation(summary = "Get budget analytics")
    public ResponseEntity<?> getBudgetAnalytics(Authentication authentication) {
        String userId = authentication.getName();
        BudgetAnalyticsResponse analytics = userAnalyticsService.getBudgetAnalytics(userId);
        return Response.renderJSON(analytics);
    }

    @GetMapping("/budget/detailed")
    @Operation(summary = "Get detailed budget analytics")
    public ResponseEntity<?> getDetailedBudgetAnalytics(Authentication authentication) {
        String userId = authentication.getName();
        BudgetAnalyticsResponse analytics = userAnalyticsService.getBudgetAnalytics(userId);
        return Response.renderJSON(analytics);
    }

    @GetMapping("/tasks/daily")
    @Operation(summary = "Get daily task analytics")
    public ResponseEntity<?> getDailyTaskAnalytics(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        String userId = authentication.getName();
        DailyTaskAnalyticsResponse analytics = userAnalyticsService.getDailyTaskAnalytics(userId, date);
        return Response.renderJSON(analytics);
    }
}