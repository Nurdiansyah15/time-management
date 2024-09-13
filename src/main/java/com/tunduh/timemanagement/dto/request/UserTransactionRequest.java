package com.tunduh.timemanagement.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserTransactionRequest {

    @NotNull(message = "Item ID is required")
    private String itemId;

    @NotNull(message = "The number of items is required")
    private Integer quantity;
}