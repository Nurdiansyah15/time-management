package com.tunduh.timemanagement.service.impl;

import com.tunduh.timemanagement.dto.request.UserTransactionRequest;
import com.tunduh.timemanagement.dto.response.ShopItemResponse;
import com.tunduh.timemanagement.dto.response.UserTransactionResponse;
import com.tunduh.timemanagement.entity.ShopItemEntity;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.entity.UserTransactionEntity;
import com.tunduh.timemanagement.exception.ResourceNotFoundException;
import com.tunduh.timemanagement.repository.ShopItemRepository;
import com.tunduh.timemanagement.repository.UserRepository;
import com.tunduh.timemanagement.repository.UserTransactionRepository;
import com.tunduh.timemanagement.service.UserTransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserTransactionServiceImpl implements UserTransactionService {

    private final UserTransactionRepository userTransactionRepository;
    private final UserRepository userRepository;
    private final ShopItemRepository shopItemRepository;

    @Override
    @Transactional
    public UserTransactionResponse createTransaction(UserTransactionRequest transactionRequest, String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ShopItemEntity shopItem = shopItemRepository.findById(transactionRequest.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Shop item not found"));

        int totalPrice = shopItem.getPrice() * transactionRequest.getQuantity();

        if (user.getUserPoint() < totalPrice) {
            throw new RuntimeException("Insufficient points for this transaction");
        }

        UserTransactionEntity transaction = UserTransactionEntity.builder()
                .id(UUID.randomUUID().toString())
                .user(user)
                .shopItem(shopItem)
                .transactionDate(LocalDateTime.now())
                .quantity(transactionRequest.getQuantity())
                .totalPrice(totalPrice)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        UserTransactionEntity savedTransaction = userTransactionRepository.save(transaction);

        // Deduct points from user
        user.setUserPoint(user.getUserPoint() - totalPrice);
        userRepository.save(user);

        return mapToUserTransactionResponse(savedTransaction);
    }

    @Override
    public List<UserTransactionResponse> getUserTransactions(String userId) {
        List<UserTransactionEntity> transactions = userTransactionRepository.findByUserId(userId);
        return transactions.stream()
                .map(this::mapToUserTransactionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserTransactionResponse getTransactionById(String id, String userId) {
        UserTransactionEntity transaction = (UserTransactionEntity) userTransactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        return mapToUserTransactionResponse(transaction);
    }

    private UserTransactionResponse mapToUserTransactionResponse(UserTransactionEntity transaction) {
        return UserTransactionResponse.builder()
                .id(transaction.getId())
                .transactionDate(transaction.getTransactionDate())
                .quantity(transaction.getQuantity())
                .totalPrice(transaction.getTotalPrice())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .shopItem(mapToShopItemResponse(transaction.getShopItem()))
                .build();
    }

    private ShopItemResponse mapToShopItemResponse(ShopItemEntity shopItem) {
        return ShopItemResponse.builder()
                .id(shopItem.getId())
                .name(shopItem.getName())
                .itemPicture(shopItem.getItemPicture())
                .price(shopItem.getPrice())
                .createdAt(shopItem.getCreatedAt())
                .updatedAt(shopItem.getUpdatedAt())
                .build();
    }
}
