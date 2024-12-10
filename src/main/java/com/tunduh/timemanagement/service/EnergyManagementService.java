package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EnergyManagementService {

    private final UserRepository userRepository;

    @Transactional
    public void decreaseEnergy(String userId, int energyToDecrease) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int newEnergy = user.getEnergy() - energyToDecrease;
        user.setEnergy(newEnergy);
        userRepository.save(user);
    }

    @Transactional
    public void increaseEnergy(String userId, int energyToIncrease) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int newEnergy = user.getEnergy() + energyToIncrease;
        user.setEnergy(newEnergy);
        userRepository.save(user);
    }
}