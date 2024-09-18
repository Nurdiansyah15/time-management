package com.tunduh.timemanagement.seeder;

import com.tunduh.timemanagement.entity.Role;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class UserSeeder implements Seeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void seed() {
        if (userRepository.count() == 0) {
            UserEntity adminUser = UserEntity.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("adminPassword"))
                    .role(Role.ROLE_ADMIN)
                    .userPoint(0)
                    .resetTime(LocalTime.of(0, 0))
                    .build();
            userRepository.save(adminUser);

            UserEntity regularUser = UserEntity.builder()
                    .username("user")
                    .email("user@example.com")
                    .password(passwordEncoder.encode("userPassword"))
                    .role(Role.ROLE_USER)
                    .userPoint(0)
                    .resetTime(LocalTime.of(0, 0))
                    .build();
            userRepository.save(regularUser);
        }
    }
}