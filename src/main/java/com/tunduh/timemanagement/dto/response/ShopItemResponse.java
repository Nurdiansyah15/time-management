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
public class ShopItemResponse implements Serializable {

    private String id;
    private String name;
    private String itemPicture;
    private Integer price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}