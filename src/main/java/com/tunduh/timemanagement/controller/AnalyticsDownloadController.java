package com.tunduh.timemanagement.controller;

import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.service.UserAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/analytics/download")
@RequiredArgsConstructor
@Slf4j
public class AnalyticsDownloadController {
    private final UserAnalyticsService userAnalyticsService;

    @GetMapping("/tasks")
    @Operation(summary = "Download task analytics as CSV")
    public ResponseEntity<byte[]> downloadTaskAnalytics(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        log.info("Downloading task analytics CSV for user {} from {} to {}", user.getId(), startDate, endDate);

        String csvData = userAnalyticsService.getTaskAnalyticsCSV(user.getId(), startDate, endDate);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=task_analytics.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData.getBytes());
    }

    @GetMapping("/missions")
    @Operation(summary = "Download mission analytics as CSV")
    public ResponseEntity<byte[]> downloadMissionAnalytics(Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        log.info("Downloading mission analytics CSV for user {}", user.getId());

        String csvData = userAnalyticsService.getMissionAnalyticsCSV(user.getId());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=mission_analytics.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData.getBytes());
    }

    @GetMapping("/budget")
    @Operation(summary = "Download budget analytics as CSV")
    public ResponseEntity<byte[]> downloadBudgetAnalytics(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        log.info("Downloading budget analytics CSV for user {} from {} to {}", user.getId(), startDate, endDate);

        String csvData = userAnalyticsService.getBudgetAnalyticsCSV(user.getId(), startDate, endDate);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=budget_analytics.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData.getBytes());
    }
}