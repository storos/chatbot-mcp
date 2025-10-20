package com.example.orderapimcp.controller;

import com.example.orderapimcp.model.Order;
import com.example.orderapimcp.service.OrderApiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/mcp")
@RequiredArgsConstructor
public class McpController {

    private final OrderApiService orderApiService;
    private final ObjectMapper objectMapper;

    @GetMapping("/tools")
    public ResponseEntity<Map<String, Object>> listTools() {
        Map<String, Object> response = new HashMap<>();
        response.put("tools", List.of(
            Map.of(
                "name", "get_all_orders",
                "description", "Tüm siparişleri listeler",
                "method", "GET",
                "endpoint", "/mcp/orders",
                "inputSchema", Map.of(
                    "type", "object",
                    "properties", Map.of(),
                    "required", List.of()
                )
            ),
            Map.of(
                "name", "get_order_by_id",
                "description", "ID'ye göre sipariş getirir",
                "method", "GET",
                "endpoint", "/mcp/orders/{id}",
                "inputSchema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "orderId", Map.of(
                            "type", "number",
                            "description", "Görüntülenecek siparişin ID'si"
                        )
                    ),
                    "required", List.of("orderId")
                )
            ),
            Map.of(
                "name", "cancel_order",
                "description", "Sipariş iptal eder",
                "method", "DELETE",
                "endpoint", "/mcp/orders/{id}",
                "inputSchema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "orderId", Map.of(
                            "type", "number",
                            "description", "İptal edilecek siparişin ID'si"
                        )
                    ),
                    "required", List.of("orderId")
                )
            ),
            Map.of(
                "name", "update_order_address",
                "description", "Siparişin teslimat adresini günceller. Kullanıcı 'ev', 'iş' gibi adres etiketlerini kullanarak adres değişikliği yapabilir.",
                "method", "PATCH",
                "endpoint", "/mcp/orders/{id}/address",
                "inputSchema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "orderId", Map.of(
                            "type", "number",
                            "description", "Adresi güncellenecek siparişin ID'si"
                        ),
                        "address", Map.of(
                            "type", "string",
                            "description", "Yeni adres etiketi (örn: 'ev', 'iş', 'ofis')"
                        )
                    ),
                    "required", List.of("orderId", "address")
                )
            )
        ));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        try {
            log.info("Creating order for customer: {}", order.getCustomerName());
            Order createdOrder = orderApiService.createOrder(order);
            return ResponseEntity.ok(createdOrder);
        } catch (Exception e) {
            log.error("Error creating order", e);
            throw new RuntimeException("Failed to create order: " + e.getMessage(), e);
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        try {
            log.info("Fetching all orders");
            List<Order> orders = orderApiService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Error fetching orders", e);
            throw new RuntimeException("Failed to fetch orders: " + e.getMessage(), e);
        }
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        try {
            log.info("Fetching order with ID: {}", id);
            Order order = orderApiService.getOrderById(id);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            log.error("Error fetching order with ID: {}", id, e);
            throw new RuntimeException("Failed to fetch order: " + e.getMessage(), e);
        }
    }

    @PutMapping("/orders/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order order) {
        try {
            log.info("Updating order with ID: {}", id);
            Order updatedOrder = orderApiService.updateOrder(id, order);
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            log.error("Error updating order with ID: {}", id, e);
            throw new RuntimeException("Failed to update order: " + e.getMessage(), e);
        }
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Map<String, String>> cancelOrder(@PathVariable Long id) {
        try {
            log.info("Cancelling order with ID: {}", id);
            orderApiService.cancelOrder(id);
            return ResponseEntity.ok(Map.of("message", String.format("Sipariş %d başarıyla iptal edildi.", id)));
        } catch (Exception e) {
            log.error("Error cancelling order with ID: {}", id, e);
            throw new RuntimeException("Failed to cancel order: " + e.getMessage(), e);
        }
    }

    @PatchMapping("/orders/{id}/address")
    public ResponseEntity<Order> updateOrderAddress(@PathVariable Long id, @RequestParam String address) {
        try {
            log.info("Updating address for order ID: {} to: {}", id, address);
            Order updatedOrder = orderApiService.updateOrderAddress(id, address);
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            log.error("Error updating address for order ID: {}", id, e);
            throw new RuntimeException("Failed to update order address: " + e.getMessage(), e);
        }
    }
}
