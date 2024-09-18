package com.tunduh.timemanagement.controller;

import com.tunduh.timemanagement.dto.response.TransactionResponse;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.service.TransactionService;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import com.tunduh.timemanagement.utils.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Transaction management operations")
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping
    @Operation(summary = "Get user transactions")
    public ResponseEntity<?> getUserTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        CustomPagination<TransactionResponse> transactions = transactionService.getUserTransactions(user.getId(), page, size);
        return Response.renderJSON(transactions);
    }

    @GetMapping("/{transactionId}")
    @Operation(summary = "Get a specific transaction")
    public ResponseEntity<?> getTransactionById(@PathVariable String transactionId, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        TransactionResponse transaction = transactionService.getTransactionById(transactionId, user.getId());
        return Response.renderJSON(transaction);
    }
}