package com.tunduh.timemanagement.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tunduh.timemanagement.dto.response.UserResponse;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.service.UserService;
import com.tunduh.timemanagement.utils.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        UserEntity principal = (UserEntity) authentication.getPrincipal();
        String userId = principal.getId();

        UserResponse userResponse = userService.getCurrentUser(userId);
        return Response.renderJSON(userResponse);
    }

    @PutMapping("/add-points")
    public ResponseEntity<?> addPoints(@RequestParam int points, Authentication authentication) {
        UserEntity principal = (UserEntity) authentication.getPrincipal();
        String userId = principal.getId();

        UserResponse updatedUser = userService.addPoints(userId, points);
        return Response.renderJSON(updatedUser, "Points added successfully!");
    }

    @PutMapping("/{id}/photos")
    @Operation(summary = "Update photo")
    public ResponseEntity<?> updatePhoto(
            @RequestPart("images") MultipartFile file,
            @PathVariable String id) throws JsonProcessingException {
        return Response.renderJSON(userService.updatePhoto(file, id), "PHOTOS UPLOADED");
    }

}