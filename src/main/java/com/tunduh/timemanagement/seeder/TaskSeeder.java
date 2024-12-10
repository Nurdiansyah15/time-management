package com.tunduh.timemanagement.seeder;

import com.tunduh.timemanagement.entity.TaskEntity;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.repository.TaskRepository;
import com.tunduh.timemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TaskSeeder implements Seeder {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    public void seed() {
        if (taskRepository.count() == 0) {
            UserEntity user = userRepository.findByUsername("user")
                    .orElseThrow(() -> new RuntimeException("User not found"));

            TaskEntity task1 = TaskEntity.builder()
                    .title("Sample Task 1")
                    .energy(50)
                    .notes("This is a sample task")
                    .status("PENDING")
                    .duration(60)
                    .priority("MEDIUM")
                    .user(user)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            taskRepository.save(task1);

            TaskEntity task2 = TaskEntity.builder()
                    .title("Sample Task 2")
                    .energy(75)
                    .notes("This is another sample task")
                    .status("IN_PROGRESS")
                    .duration(120)
                    .priority("HIGH")
                    .user(user)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            taskRepository.save(task2);
        }
    }
}