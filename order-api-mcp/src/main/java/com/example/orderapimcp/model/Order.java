package com.example.orderapimcp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class Order {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("customerName")
    private String customerName;

    @JsonProperty("customerEmail")
    private String customerEmail;

    @JsonProperty("items")
    private List<OrderItem> items;

    @JsonProperty("totalAmount")
    private BigDecimal totalAmount;

    @JsonProperty("status")
    private OrderStatus status;

    @JsonProperty("address")
    private String address;

    @JsonProperty("orderDate")
    private LocalDateTime orderDate;
}
