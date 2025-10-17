package com.example.orderapi.service;

import com.example.orderapi.exception.OrderNotFoundException;
import com.example.orderapi.model.Order;
import com.example.orderapi.model.OrderItem;
import com.example.orderapi.model.OrderStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    /**
     * Create a new order - returns dummy success response
     */
    public Order createOrder(Order order) {
        // Generate dummy order with ID and timestamp
        order.setId(generateDummyId());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        return order;
    }

    /**
     * Get all orders - returns dummy list
     */
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();

        // Create dummy order 1
        orders.add(Order.builder()
                .id(1L)
                .customerName("John Doe")
                .customerEmail("john.doe@example.com")
                .items(List.of(
                        OrderItem.builder()
                                .itemName("Laptop")
                                .quantity(1)
                                .price(new BigDecimal("999.99"))
                                .build(),
                        OrderItem.builder()
                                .itemName("Mouse")
                                .quantity(2)
                                .price(new BigDecimal("25.50"))
                                .build()
                ))
                .totalAmount(new BigDecimal("1050.99"))
                .status(OrderStatus.CONFIRMED)
                .address("ev")
                .orderDate(LocalDateTime.now().minusDays(2))
                .build());

        // Create dummy order 2
        orders.add(Order.builder()
                .id(2L)
                .customerName("Jane Smith")
                .customerEmail("jane.smith@example.com")
                .items(List.of(
                        OrderItem.builder()
                                .itemName("Keyboard")
                                .quantity(1)
                                .price(new BigDecimal("75.00"))
                                .build()
                ))
                .totalAmount(new BigDecimal("75.00"))
                .status(OrderStatus.SHIPPED)
                .address("iş")
                .orderDate(LocalDateTime.now().minusDays(1))
                .build());

        return orders;
    }

    /**
     * Get order by ID - returns dummy order
     */
    public Order getOrderById(Long id) {
        if (id == null || id <= 0) {
            throw new OrderNotFoundException("Order with ID " + id + " not found");
        }

        // Return dummy order
        return Order.builder()
                .id(id)
                .customerName("John Doe")
                .customerEmail("john.doe@example.com")
                .items(List.of(
                        OrderItem.builder()
                                .itemName("Sample Product")
                                .quantity(1)
                                .price(new BigDecimal("99.99"))
                                .build()
                ))
                .totalAmount(new BigDecimal("99.99"))
                .status(OrderStatus.PENDING)
                .address("ev")
                .orderDate(LocalDateTime.now())
                .build();
    }

    /**
     * Update order - returns dummy updated order
     */
    public Order updateOrder(Long id, Order order) {
        if (id == null || id <= 0) {
            throw new OrderNotFoundException("Order with ID " + id + " not found");
        }

        // Return updated dummy order
        order.setId(id);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    /**
     * Delete order - simulates successful deletion
     */
    public void deleteOrder(Long id) {
        if (id == null || id <= 0) {
            throw new OrderNotFoundException("Order with ID " + id + " not found");
        }
        // Simulate successful deletion (do nothing)
    }

    /**
     * Update order address - returns order with updated address
     */
    public Order updateAddress(Long id, String address) {
        if (id == null || id <= 0) {
            throw new OrderNotFoundException("Order with ID " + id + " not found");
        }

        // Return dummy order with updated address
        return Order.builder()
                .id(id)
                .customerName("John Doe")
                .customerEmail("john.doe@example.com")
                .items(List.of(
                        OrderItem.builder()
                                .itemName("Sample Product")
                                .quantity(1)
                                .price(new BigDecimal("99.99"))
                                .build()
                ))
                .totalAmount(new BigDecimal("99.99"))
                .status(OrderStatus.PENDING)
                .address(address)
                .orderDate(LocalDateTime.now())
                .build();
    }

    /**
     * Generate a dummy ID
     */
    private Long generateDummyId() {
        return System.currentTimeMillis() % 10000;
    }
}
