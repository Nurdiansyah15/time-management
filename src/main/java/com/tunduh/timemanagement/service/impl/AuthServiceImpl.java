package com.tunduh.timemanagement.service.impl;

import com.tunduh.timemanagement.dto.request.LoginRequest;
import com.tunduh.timemanagement.dto.request.RegisterRequest;
import com.tunduh.timemanagement.dto.response.AuthResponse;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.entity.Role;
import com.tunduh.timemanagement.repository.UserRepository;
import com.tunduh.timemanagement.security.JwtTokenProvider;
import com.tunduh.timemanagement.security.CustomOAuth2User;
import com.tunduh.timemanagement.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private static final int MIN_PASSWORD_LENGTH = 8;


    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserEntity registerUser(RegisterRequest registerRequest) {
        if (existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        UserEntity user = new UserEntity();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.ROLE_USER);

        return userRepository.save(user);
    }

    @Override
    public UserEntity getOrCreateUser(String email, String name) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    UserEntity newUser = new UserEntity();
                    newUser.setEmail(email);
                    newUser.setUsername(name);
                    newUser.setRole(Role.ROLE_USER);
                    return userRepository.save(newUser);
                });
    }

    @Override
    public String createToken(UserEntity user) {
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(user, Collections.emptyMap());
        Authentication authentication = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenProvider.createToken(authentication.getPrincipal().toString());
    }

    @Override
    public AuthResponse login(LoginRequest req) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );

            String accessToken = jwtTokenProvider.createToken(((UserEntity) auth.getPrincipal()).getId());
            String refreshToken = jwtTokenProvider.createToken(((UserEntity) auth.getPrincipal()).getId());
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
                .password(passwordEncoder.encode(req.getPassword()))
                .build();
        UserEntity userResult = userRepository.save(user);

        String accessToken = jwtTokenProvider.createToken(userResult.getId());
        String refreshToken = jwtTokenProvider.createToken(userResult.getId());

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
