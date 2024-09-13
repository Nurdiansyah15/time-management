package com.tunduh.timemanagement.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
 @NotBlank(message = "Username must be entered")
 private String username;

 @NotBlank(message = "Email required")
 @Email(message = "Invalid email format")
 private String email;

 @NotBlank(message = "Password must be entered")
 private String password;
}