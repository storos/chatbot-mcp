package com.example.orderapi.controller;

import com.example.orderapi.model.Order;
import com.example.orderapi.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "APIs for managing orders")
public class OrderController {

    private final OrderService orderService;

    @Operation(
            summary = "Create a new order",
            description = "Creates a new order with the provided details. Returns the created order with a generated ID and timestamp."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Order created successfully",
                    content = @Content(schema = @Schema(implementation = Order.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) {
        log.info("POST /api/orders - Request: {}", order);
        Order createdOrder = orderService.createOrder(order);
        log.info("POST /api/orders - Response: {}", createdOrder);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get all orders",
            description = "Retrieves a list of all orders in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved list of orders",
                    content = @Content(schema = @Schema(implementation = Order.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        log.info("GET /api/orders - Retrieving all orders");
        List<Order> orders = orderService.getAllOrders();
        log.info("GET /api/orders - Response: {} orders found", orders.size());
        return ResponseEntity.ok(orders);
    }

    @Operation(
            summary = "Get order by ID",
            description = "Retrieves a specific order by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order found",
                    content = @Content(schema = @Schema(implementation = Order.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(
            @Parameter(description = "ID of the order to retrieve", required = true)
            @PathVariable Long id) {
        log.info("GET /api/orders/{} - Request", id);
        Order order = orderService.getOrderById(id);
        log.info("GET /api/orders/{} - Response: {}", id, order);
        return ResponseEntity.ok(order);
    }

    @Operation(
            summary = "Update an existing order",
            description = "Updates an existing order with the provided details. Returns the updated order."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order updated successfully",
                    content = @Content(schema = @Schema(implementation = Order.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(
            @Parameter(description = "ID of the order to update", required = true)
            @PathVariable Long id,
            @Valid @RequestBody Order order) {
        log.info("PUT /api/orders/{} - Request: {}", id, order);
        Order updatedOrder = orderService.updateOrder(id, order);
        log.info("PUT /api/orders/{} - Response: {}", id, updatedOrder);
        return ResponseEntity.ok(updatedOrder);
    }

    @Operation(
            summary = "Delete an order",
            description = "Deletes an order by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Order deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "ID of the order to delete", required = true)
            @PathVariable Long id) {
        log.info("DELETE /api/orders/{} - Request", id);
        orderService.deleteOrder(id);
        log.info("DELETE /api/orders/{} - Successfully deleted", id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Update order address",
            description = "Updates the delivery address of an existing order"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Address updated successfully",
                    content = @Content(schema = @Schema(implementation = Order.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content
            )
    })
    @PatchMapping("/{id}/address")
    public ResponseEntity<Order> updateAddress(
            @Parameter(description = "ID of the order to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "New delivery address (e.g., 'ev', 'iş')", required = true)
            @RequestParam String address) {
        log.info("PATCH /api/orders/{}/address - Request: address={}", id, address);
        Order updatedOrder = orderService.updateAddress(id, address);
        log.info("PATCH /api/orders/{}/address - Response: {}", id, updatedOrder);
        return ResponseEntity.ok(updatedOrder);
    }
}
