package com.tunduh.timemanagement.controller;

import com.tunduh.timemanagement.dto.request.UserUpdateRequest;
import com.tunduh.timemanagement.dto.response.UserResponse;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.service.UserService;
import com.tunduh.timemanagement.utils.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current user information")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        logger.info("Fetching current user information for user {}", user.getId());
        UserResponse userResponse = userService.getCurrentUser(user.getId());
        return Response.renderJSON(userResponse);
    }

    @PutMapping("/update")
    @Operation(summary = "Update user information")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateRequest updateRequest, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        logger.info("Updating user information for user {}", user.getId());
        UserResponse updatedUser = userService.updateUser(user.getId(), updateRequest);
        return Response.renderJSON(updatedUser, "User information updated successfully");
    }

    @PutMapping("/reset-time")
    @Operation(summary = "Set daily reset time")
    public ResponseEntity<?> setResetTime(@RequestParam String time, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        LocalTime resetTime = LocalTime.parse(time);
        logger.info("Setting reset time to {} for user {}", resetTime, user.getId());
        UserResponse updatedUser = userService.setResetTime(user.getId(), resetTime);
        return Response.renderJSON(updatedUser, "Reset time set successfully");
    }

    @PutMapping("/add-points")
    @Operation(summary = "Add points to user")
    public ResponseEntity<?> addPoints(@RequestParam int points, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        logger.info("Adding {} points to user {}", points, user.getId());
        UserResponse updatedUser = userService.addPoints(user.getId(), points);
        return Response.renderJSON(updatedUser, "Points added successfully");
    }

    @PutMapping("/photo")
    @Operation(summary = "Update user profile photo")
    public ResponseEntity<?> updatePhoto(@RequestPart("image") MultipartFile file, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        logger.info("Updating profile photo for user {}", user.getId());
        UserResponse updatedUser = userService.updatePhoto(file, user.getId());
        return Response.renderJSON(updatedUser, "Profile photo updated successfully");
    }
}