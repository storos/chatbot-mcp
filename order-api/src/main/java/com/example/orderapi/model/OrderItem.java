package com.example.orderapi.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Item within an order")
public class OrderItem {

    @Schema(description = "Name of the item", example = "Laptop", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Item name is required")
    private String itemName;

    @Schema(description = "Quantity of the item", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    @Schema(description = "Price per unit", example = "999.99", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;
}
