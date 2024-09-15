package com.tunduh.timemanagement.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PurchaseResponse {
    private String transactionId;
    private String itemName;
    private int quantity;
    private int totalPrice;
    private LocalDateTime purchaseDate;
}