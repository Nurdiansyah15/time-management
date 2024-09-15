package com.tunduh.timemanagement.controller;

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

    @GetMapping("/dashboard")
    @Operation(summary = "Get user analytics dashboard")
    public ResponseEntity<?> getUserAnalyticsDashboard(Authentication authentication) {
        String userId = authentication.getName();
        UserAnalyticsResponse analytics = userAnalyticsService.getUserAnalyticsDashboard(userId);
        return Response.renderJSON(analytics);
    }

    @GetMapping("/tasks")
    @Operation(summary = "Get task analytics")
    public ResponseEntity<?> getTaskAnalytics(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String userId = authentication.getName();
        UserAnalyticsResponse analytics = userAnalyticsService.getTaskAnalytics(userId, startDate, endDate);
        return Response.renderJSON(analytics);
    }

    @GetMapping("/missions")
    @Operation(summary = "Get mission analytics")
    public ResponseEntity<?> getMissionAnalytics(Authentication authentication) {
        String userId = authentication.getName();
        UserAnalyticsResponse analytics = userAnalyticsService.getMissionAnalytics(userId);
        return Response.renderJSON(analytics);
    }

    @GetMapping("/budget")
    @Operation(summary = "Get budget analytics")
    public ResponseEntity<?> getBudgetAnalytics(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String userId = authentication.getName();
        UserAnalyticsResponse analytics = userAnalyticsService.getBudgetAnalytics(userId, startDate, endDate);
        return Response.renderJSON(analytics);
    }
}