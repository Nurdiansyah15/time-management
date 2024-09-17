package com.tunduh.timemanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShopItemRequest {

    @NotBlank(message = "Item name is required")
    private String name;

    @NotNull(message = "Item price must be entered")
    private Integer price;

    private Integer stock;

    @NotBlank(message = "Item type is required")
    private String type;
}