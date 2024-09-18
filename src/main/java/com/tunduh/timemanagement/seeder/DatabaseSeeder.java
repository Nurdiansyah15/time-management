package com.tunduh.timemanagement.seeder;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.db.run-seeders", havingValue = "true")
public class DatabaseSeeder implements CommandLineRunner {

    private final UserSeeder userSeeder;
    private final TaskSeeder taskSeeder;
    private final MissionSeeder missionSeeder;

    @Override
    public void run(String... args) {
        userSeeder.seed();
        taskSeeder.seed();
        missionSeeder.seed();
    }
}