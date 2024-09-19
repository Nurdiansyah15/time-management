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
                    .status("ACTIVE")
                    .pointReward(100)
                    .requiredTaskCount(5)
                    .requiredDuration(0)
                    .isDurationOnly(false)
                    .isTaskOnly(true)
                    .isRewardClaimed(false)
                    .isClaimed(false)
                    .missionPicture("")
                    .startDate(LocalDateTime.now())
                    .endDate(LocalDateTime.now())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            missionRepository.save(mission1);

            MissionEntity mission2 = MissionEntity.builder()
                    .name("Work for 2 Hours")
                    .description("Accumulate 2 hours of work time")
                    .status("ACTIVE")
                    .pointReward(150)
                    .requiredTaskCount(0)
                    .requiredDuration(120)
                    .isDurationOnly(true)
                    .isTaskOnly(false)
                    .isClaimed(false)
                    .isRewardClaimed(false)
                    .missionPicture("")
                    .startDate(LocalDateTime.now())
                    .endDate(LocalDateTime.now())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            missionRepository.save(mission2);
        }
    }
}