package com.example.orderapi.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order entity representing a customer order")
public class Order {

    @Schema(description = "Unique identifier of the order", example = "1234", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Name of the customer", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Customer name is required")
    private String customerName;

    @Schema(description = "Email address of the customer", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    private String customerEmail;

    @Schema(description = "List of items in the order", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItem> items;

    @Schema(description = "Total amount of the order", example = "1050.99", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Total amount is required")
    private BigDecimal totalAmount;

    @Schema(description = "Current status of the order", example = "PENDING", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Order status is required")
    private OrderStatus status;

    @Schema(description = "Delivery address for the order", example = "ev")
    private String address;

    @Schema(description = "Timestamp when the order was created", example = "2025-01-15T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime orderDate;
}
