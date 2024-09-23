package com.tunduh.timemanagement.controller;

import com.tunduh.timemanagement.dto.request.AnalyticsRequest;
import com.tunduh.timemanagement.dto.response.AnalyticsDataOverallResponse;
import com.tunduh.timemanagement.dto.response.AnalyticsDataResponse;
import com.tunduh.timemanagement.dto.response.OverallStats;
import com.tunduh.timemanagement.dto.response.UserAnalyticsResponse;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.service.AnalyticsService;
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
    private final AnalyticsService analyticsService;

    @GetMapping
    public ResponseEntity<AnalyticsDataResponse> getAnalytics(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        AnalyticsDataResponse analyticsDTO = analyticsService.getAnalytics(user.getId(), startDate, endDate);
        return ResponseEntity.ok(analyticsDTO);
    }

    @GetMapping("/overall")
    public ResponseEntity<OverallStats> getOverallAnalytics(Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        OverallStats analyticsDTO = analyticsService.getOverallStats(user.getId());
        return ResponseEntity.ok(analyticsDTO);
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get user analytics dashboard")
    public ResponseEntity<?> getUserAnalyticsDashboard(Authentication authentication) {
        UserEntity principal = (UserEntity) authentication.getPrincipal();
        String userId = principal.getId();

        UserAnalyticsResponse analytics = userAnalyticsService.getUserAnalyticsDashboard(userId);
        return Response.renderJSON(analytics);
    }

    @GetMapping("/tasks")
    @Operation(summary = "Get task analytics")
    public ResponseEntity<?> getTaskAnalytics(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        UserEntity principal = (UserEntity) authentication.getPrincipal();
        String userId = principal.getId();

        UserAnalyticsResponse analytics = userAnalyticsService.getTaskAnalytics(userId, startDate, endDate);
        return Response.renderJSON(analytics);
    }

    @GetMapping("/missions")
    @Operation(summary = "Get mission analytics")
    public ResponseEntity<?> getMissionAnalytics(Authentication authentication) {
        UserEntity principal = (UserEntity) authentication.getPrincipal();
        String userId = principal.getId();

        UserAnalyticsResponse analytics = userAnalyticsService.getMissionAnalytics(userId);
        return Response.renderJSON(analytics);
    }

    @GetMapping("/budget")
    @Operation(summary = "Get budget analytics")
    public ResponseEntity<?> getBudgetAnalytics(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        UserEntity principal = (UserEntity) authentication.getPrincipal();
        String userId = principal.getId();

        UserAnalyticsResponse analytics = userAnalyticsService.getBudgetAnalytics(userId, startDate, endDate);
        return Response.renderJSON(analytics);
    }
}