package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.request.UserTransactionRequest;
import com.tunduh.timemanagement.dto.response.UserTransactionResponse;

import java.util.List;

public interface UserTransactionService {

    UserTransactionResponse createTransaction(UserTransactionRequest transactionRequest, String userId);

    List<UserTransactionResponse> getUserTransactions(String userId);

    UserTransactionResponse getTransactionById(String id, String userId);
}
