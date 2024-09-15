package com.tunduh.timemanagement.scheduler;

import com.tunduh.timemanagement.entity.TaskEntity;
import com.tunduh.timemanagement.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskScheduler {

    private final TaskRepository taskRepository;

    @Scheduled(cron = "0 0 0 * * ?") // Run every day at midnight
    @Transactional
    public void createRecurringTasks() {
        log.info("Starting creation of recurring tasks");
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        createDailyTasks(now);
        createWeeklyTasks(now);
        createMonthlyTasks(now);
        createYearlyTasks(now);

        log.info("Finished creating recurring tasks");
    }

    private void createDailyTasks(LocalDateTime now) {
        List<TaskEntity> dailyTasks = taskRepository.findDailyRecurringTasks(now);
        for (TaskEntity task : dailyTasks) {
            createNewTaskInstance(task, now);
        }
    }

    private void createWeeklyTasks(LocalDateTime now) {
        int dayOfWeek = now.getDayOfWeek().getValue();
        List<TaskEntity> weeklyTasks = taskRepository.findWeeklyRecurringTasks(now, dayOfWeek);
        for (TaskEntity task : weeklyTasks) {
            createNewTaskInstance(task, now);
        }
    }

    private void createMonthlyTasks(LocalDateTime now) {
        List<TaskEntity> monthlyTasks = taskRepository.findMonthlyRecurringTasks(now);
        for (TaskEntity task : monthlyTasks) {
            createNewTaskInstance(task, now);
        }
    }

    private void createYearlyTasks(LocalDateTime now) {
        List<TaskEntity> yearlyTasks = taskRepository.findYearlyRecurringTasks(now);
        for (TaskEntity task : yearlyTasks) {
            createNewTaskInstance(task, now);
        }
    }

    private void createNewTaskInstance(TaskEntity originalTask, LocalDateTime now) {
        TaskEntity newTask = TaskEntity.builder()
                .id(UUID.randomUUID().toString())
                .title(originalTask.getTitle())
                .energy(originalTask.getEnergy())
                .notes(originalTask.getNotes())
                .status("PENDING")
                .duration(originalTask.getDuration())
                .priority(originalTask.getPriority())
                .user(originalTask.getUser())
                .createdAt(now)
                .updatedAt(now)
                .build();

        taskRepository.save(newTask);
        log.info("Created new task instance: {}", newTask.getId());
    }
}