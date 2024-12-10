package com.tunduh.timemanagement.controller;

import com.tunduh.timemanagement.dto.request.LoginRequest;
import com.tunduh.timemanagement.dto.request.RegisterRequest;
import com.tunduh.timemanagement.dto.response.AuthResponse;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.repository.UserRepository;
import com.tunduh.timemanagement.service.AuthService;
import com.tunduh.timemanagement.utils.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return Collections.singletonMap("name", principal.getAttribute("name"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        log.info("Login request: {}", req.getEmail());
        return Response.renderJSON(
                authService.login(req)
        );
    }


    @PostMapping("/oauth2/token")
    public ResponseEntity<?> handleOAuth2Login(@RequestParam("token") String token) {
        return Response.renderJSON(
                authService.handleGoogleSignIn(token)
        );
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        return Response.renderJSON(
                authService.register(req),
                "Success",
                HttpStatus.CREATED
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        return Response.renderJSON(
                authService.refreshToken(refreshToken)
        );
    }

}