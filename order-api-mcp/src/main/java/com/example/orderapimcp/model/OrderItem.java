package com.example.orderapimcp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @JsonProperty("itemName")
    private String itemName;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("price")
    private BigDecimal price;
}
