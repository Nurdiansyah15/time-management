package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.response.TransactionResponse;
import com.tunduh.timemanagement.entity.TransactionEntity;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.exception.ResourceNotFoundException;
import com.tunduh.timemanagement.repository.TransactionRepository;
import com.tunduh.timemanagement.repository.UserRepository;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public TransactionResponse createTransaction(String userId, Integer pointsChange, TransactionEntity.TransactionType type, String description) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        TransactionEntity transaction = TransactionEntity.builder()
                .user(user)
                .pointsChange(pointsChange)
                .type(type)
                .description(description)
                .build();

        TransactionEntity savedTransaction = transactionRepository.save(transaction);

        // Update user points
        user.setUserPoint(user.getUserPoint() + pointsChange);
        userRepository.save(user);

        return mapToTransactionResponse(savedTransaction);
    }

    @Override
    public CustomPagination<TransactionResponse> getUserTransactions(String userId, int page, int size) {
        Page<TransactionEntity> transactionPage = transactionRepository.findByUserId(userId, PageRequest.of(page, size));
        return new CustomPagination<>(transactionPage.map(this::mapToTransactionResponse));
    }

    @Override
    public TransactionResponse getTransactionById(String transactionId, String userId) {
        TransactionEntity transaction = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        return mapToTransactionResponse(transaction);
    }

    private TransactionResponse mapToTransactionResponse(TransactionEntity transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .userId(transaction.getUser().getId())
                .pointsChange(transaction.getPointsChange())
                .type(transaction.getType())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}