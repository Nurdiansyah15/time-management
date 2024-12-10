package com.tunduh.timemanagement.seeder;

import com.tunduh.timemanagement.entity.MissionEntity;
import com.tunduh.timemanagement.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class MissionSeeder implements Seeder {
    private final MissionRepository missionRepository;

    @Override
    public void seed() {
        if (missionRepository.count() == 0) {
            MissionEntity mission1 = MissionEntity.builder()
                    .name("Complete 5 Tasks")
                    .description("Complete 5 tasks to earn bonus points")
                    .status(MissionEntity.MissionStatus.ACTIVE)
                    .pointReward(100)
                    .criteriaCompleted(0)
                    .criteriaValue(10)
                    .type(MissionEntity.Type.TASK_BASED)
                    .startDate(LocalDateTime.now())
                    .endDate(LocalDateTime.now().plusDays(7))
                    .build();
            missionRepository.save(mission1);

            MissionEntity mission2 = MissionEntity.builder()
                    .name("Work for 2 Hours")
                    .description("Accumulate 2 hours of work time")
                    .status(MissionEntity.MissionStatus.ACTIVE)
                    .pointReward(150)
                    .criteriaCompleted(0)
                    .criteriaValue(10)
                    .type(MissionEntity.Type.POINT_BASED)
                    .startDate(LocalDateTime.now())
                    .endDate(LocalDateTime.now().plusDays(7))
                    .build();
            missionRepository.save(mission2);
        }
    }
}