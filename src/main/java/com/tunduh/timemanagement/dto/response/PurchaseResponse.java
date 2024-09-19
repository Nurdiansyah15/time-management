package com.tunduh.timemanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseResponse implements Serializable {
    private String id;
    private String userId;
    private String shopItemId;
    private String shopItemName;
    private Integer quantity;
    private Integer totalPrice;
    private LocalDateTime createdAt;
}