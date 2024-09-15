package com.tunduh.timemanagement.controller;

import com.tunduh.timemanagement.dto.request.UserTransactionRequest;
import com.tunduh.timemanagement.dto.response.UserTransactionResponse;
import com.tunduh.timemanagement.entity.UserEntity;
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
        UserEntity principal = (UserEntity) authentication.getPrincipal();
        String userId = principal.getId();

        UserTransactionResponse createdTransaction = userTransactionService.createTransaction(transactionRequest, userId);
        return Response.renderJSON(createdTransaction, "Transaction created successfully!");
    }

    @GetMapping
    public ResponseEntity<?> getUserTransactions(Authentication authentication) {
        UserEntity principal = (UserEntity) authentication.getPrincipal();
        String userId = principal.getId();

        List<UserTransactionResponse> transactions = userTransactionService.getUserTransactions(userId);
        return Response.renderJSON(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable String id, Authentication authentication) {
        UserEntity principal = (UserEntity) authentication.getPrincipal();
        String userId = principal.getId();

        UserTransactionResponse transaction = userTransactionService.getTransactionById(id, userId);
        return Response.renderJSON(transaction);
    }
}