package com.tunduh.timemanagement.service.impl;

import com.tunduh.timemanagement.dto.request.LoginRequest;
import com.tunduh.timemanagement.dto.request.RegisterRequest;
import com.tunduh.timemanagement.dto.response.AuthResponse;
import com.tunduh.timemanagement.entity.Role;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.repository.UserRepository;
import com.tunduh.timemanagement.security.JwtTokenProvider;
import com.tunduh.timemanagement.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final int MIN_PASSWORD_LENGTH = 8;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public AuthResponse login(LoginRequest req) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );

            String accessToken = jwtTokenProvider.createToken(((UserEntity) auth.getPrincipal()).getId());
            String refreshToken = jwtTokenProvider.createRefreshToken(((UserEntity) auth.getPrincipal()).getId());

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        } catch (Exception e) {
            System.out.println(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during authentication");
        }
    }

    @Override
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Username is already taken");
        }

        List<String> passwordErrors = validatePassword(req.getPassword());
        if (!passwordErrors.isEmpty()) {
            throw new IllegalArgumentException("Password does not meet the requirements : " + String.join(", ", passwordErrors));
        }

        Role role = Role.ROLE_USER;

        UserEntity user = UserEntity.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .role(role)
                .energy(100)
                .userPoint(0)
                .password(passwordEncoder.encode(req.getPassword()))
                .build();

        UserEntity userResult = userRepository.save(user);

        String accessToken = jwtTokenProvider.createToken(userResult.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(userResult.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if (jwtTokenProvider.validateToken(refreshToken.substring("Bearer ".length()))) {
            String userId = jwtTokenProvider.getUserIdFromToken(refreshToken.substring("Bearer ".length()));
            System.out.println("userId" + userId);
            UserEntity userEntity = userRepository.findById(userId)
                    .map(user -> UserEntity.builder()
                            .id(user.getUsername())
                            .password(user.getPassword())
                            .role(user.getRole())
                            .build())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            String accessToken = jwtTokenProvider.createRefreshToken(userEntity.getId());
            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } else {
            throw new RuntimeException("Invalid refresh token");
        }
    }

    private List<String> validatePassword(String password) {
        List<String> errors = new ArrayList<>();

        if (password.length() < MIN_PASSWORD_LENGTH) {
            errors.add("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
        }
        if (!password.matches(".*[A-Z].*")) {
            errors.add("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[a-z].*")) {
            errors.add("Password must contain at least one lowercase letter");
        }
        if (!password.matches(".*\\d.*")) {
            errors.add("Password must contain at least one digit");
        }
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            errors.add("Password must contain at least one special character");
        }

        return errors;
    }

}
