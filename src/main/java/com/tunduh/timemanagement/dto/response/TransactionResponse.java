package com.tunduh.timemanagement.dto.response;

import com.tunduh.timemanagement.entity.TransactionEntity;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class TransactionResponse implements Serializable {
    private String id;
    private String userId;
    private Integer pointsChange;
    private TransactionEntity.TransactionType type;
    private String description;
    private LocalDateTime createdAt;
}