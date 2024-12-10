package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.response.TransactionResponse;
import com.tunduh.timemanagement.entity.TransactionEntity;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;

public interface TransactionService {
    TransactionResponse createTransaction(String userId, Integer pointsChange, TransactionEntity.TransactionType type, String description);
    CustomPagination<TransactionResponse> getUserTransactions(String userId, int page, int size);
    TransactionResponse getTransactionById(String transactionId, String userId);
}