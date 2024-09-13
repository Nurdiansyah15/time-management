package com.tunduh.timemanagement.controller;

import com.tunduh.timemanagement.dto.response.UserResponse;
import com.tunduh.timemanagement.service.UserService;
import com.tunduh.timemanagement.utils.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        String userId = authentication.getName();
        UserResponse userResponse = userService.getCurrentUser(userId);
        return Response.renderJSON(userResponse);
    }

    @PutMapping("/add-points")
    public ResponseEntity<?> addPoints(@RequestParam int points, Authentication authentication) {
        String userId = authentication.getName();
        UserResponse updatedUser = userService.addPoints(userId, points);
        return Response.renderJSON(updatedUser, "Points added successfully!");
    }
}