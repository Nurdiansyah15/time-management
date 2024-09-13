package com.tunduh.timemanagement.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class AuthController {

    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return Collections.singletonMap("name", principal.getAttribute("name"));
    }

    @GetMapping("/")
    public String home() {
        return "Home Page";
    }

    @GetMapping("/login")
    public String login() {
        return "Login Page";
    }

    @GetMapping("/home")
    public String securedHome() {
        return "Secured Home Page";
    }
}