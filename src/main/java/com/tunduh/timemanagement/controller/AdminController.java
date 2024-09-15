package com.tunduh.timemanagement.controller;

import com.tunduh.timemanagement.dto.request.SubmissionRequest;
import com.tunduh.timemanagement.dto.response.AnalyticsResponse;
import com.tunduh.timemanagement.dto.response.ShopItemResponse;
import com.tunduh.timemanagement.dto.response.SubmissionResponse;
import com.tunduh.timemanagement.service.AdminService;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import com.tunduh.timemanagement.utils.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Admin dashboard operations")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/submissions")
    @Operation(summary = "Create a new submission")
    public ResponseEntity<?> createSubmission(@Valid @RequestBody SubmissionRequest submissionRequest) {
        SubmissionResponse createdSubmission = adminService.createSubmission(submissionRequest);
        return Response.renderJSON(createdSubmission, "Submission created successfully!");
    }

    @GetMapping("/submissions")
    @Operation(summary = "Get all submissions with pagination and filtering")
    public ResponseEntity<?> getAllSubmissions(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort parameter (e.g., 'title,asc' or 'createdAt,desc')") @RequestParam(required = false) String sort,
            @Parameter(description = "Filter by title") @RequestParam(required = false) String title,
            @Parameter(description = "Filter by status") @RequestParam(required = false) String status
    ) {
        CustomPagination<SubmissionResponse> submissions = adminService.getAllSubmissions(page, size, sort, title, status);
        return Response.renderJSON(submissions);
    }

    @GetMapping("/submissions/{id}")
    @Operation(summary = "Get a submission by ID")
    public ResponseEntity<?> getSubmissionById(@PathVariable String id) {
        SubmissionResponse submission = adminService.getSubmissionById(id);
        return Response.renderJSON(submission);
    }

    @PutMapping("/submissions/{id}")
    @Operation(summary = "Update a submission")
    public ResponseEntity<?> updateSubmission(@PathVariable String id, @Valid @RequestBody SubmissionRequest submissionRequest) {
        SubmissionResponse updatedSubmission = adminService.updateSubmission(id, submissionRequest);
        return Response.renderJSON(updatedSubmission, "Submission updated successfully!");
    }

    @DeleteMapping("/submissions/{id}")
    @Operation(summary = "Delete a submission")
    public ResponseEntity<?> deleteSubmission(@PathVariable String id) {
        adminService.deleteSubmission(id);
        return Response.renderJSON(null, "Submission deleted successfully!");
    }

    @GetMapping("/analytics/users")
    @Operation(summary = "Get user analytics")
    public ResponseEntity<?> getUserAnalytics() {
        AnalyticsResponse analytics = adminService.getUserAnalytics();
        return Response.renderJSON(analytics);
    }

    @GetMapping("/shop")
    @Operation(summary = "Get all shop items")
    public ResponseEntity<?> getAllShopItems() {
        List<ShopItemResponse> shopItems = adminService.getAllShopItems();
        return Response.renderJSON(shopItems);
    }
}