package com.tunduh.timemanagement.controller;

import com.tunduh.timemanagement.dto.response.ShopItemResponse;
import com.tunduh.timemanagement.dto.response.UserResponse;
import com.tunduh.timemanagement.service.AdminService;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import com.tunduh.timemanagement.utils.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Admin dashboard operations")
public class AdminController {

    private final AdminService adminService;

//    @GetMapping("/analytics/users")
//    @Operation(summary = "Get user analytics")
//    public ResponseEntity<?> getUserAnalytics() {
//        AnalyticsResponse analytics = adminService.getUserAnalytics();
//        return Response.renderJSON(analytics);
//    }

    @GetMapping
    @Operation(summary = "Get all users with pagination")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "username,asc") String[] sort) {

        Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortObj = Sort.by(direction, sort[0]);
        Pageable pageable = PageRequest.of(page, size, sortObj);

        CustomPagination<UserResponse> users = adminService.getAllUsers(pageable);
        return Response.renderJSON(users);
    }

    @GetMapping("/shop")
    @Operation(summary = "Get all shop items with pagination and filtering")
    public ResponseEntity<?> getAllShopItems(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort parameter (e.g., 'name,asc' or 'price,desc')") @RequestParam(required = false) String sort,
            @Parameter(description = "Filter by name") @RequestParam(required = false) String name,
            @Parameter(description = "Filter by max price") @RequestParam(required = false) Integer maxPrice) {
        CustomPagination<ShopItemResponse> shopItems = adminService.getAllShopItems(page, size, sort, name, maxPrice);
        return Response.renderJSON(shopItems);
    }
}