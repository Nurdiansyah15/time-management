package com.tunduh.timemanagement.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PurchaseResponse {
    private String id;
    private String userId;
    private String shopItemId;
    private String shopItemName;
    private Integer quantity;
    private Integer totalPrice;
    private LocalDateTime createdAt;
}