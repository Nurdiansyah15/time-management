package com.tunduh.timemanagement.controller;

import com.tunduh.timemanagement.dto.request.UserTransactionRequest;
import com.tunduh.timemanagement.dto.response.UserTransactionResponse;
import com.tunduh.timemanagement.service.UserTransactionService;
import com.tunduh.timemanagement.utils.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class UserTransactionController {

    private final UserTransactionService userTransactionService;

    @PostMapping
    public ResponseEntity<?> createTransaction(@Valid @RequestBody UserTransactionRequest transactionRequest, Authentication authentication) {
        String userId = authentication.getName();
        UserTransactionResponse createdTransaction = userTransactionService.createTransaction(transactionRequest, userId);
        return Response.renderJSON(createdTransaction, "Transaction created successfully!");
    }

    @GetMapping
    public ResponseEntity<?> getUserTransactions(Authentication authentication) {
        String userId = authentication.getName();
        List<UserTransactionResponse> transactions = userTransactionService.getUserTransactions(userId);
        return Response.renderJSON(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable String id, Authentication authentication) {
        String userId = authentication.getName();
        UserTransactionResponse transaction = userTransactionService.getTransactionById(id, userId);
        return Response.renderJSON(transaction);
    }
}