package com.example.orderapimcp.service;

import com.example.orderapimcp.config.OrderApiConfig;
import com.example.orderapimcp.model.Order;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderApiService {

    private final OrderApiConfig config;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public Order createOrder(Order order) {
        try {
            log.info("Creating order for customer: {}", order.getCustomerName());
            HttpEntity<Order> request = new HttpEntity<>(order, createHeaders());
            ResponseEntity<Order> response = restTemplate.exchange(
                config.getBaseUrl(),
                HttpMethod.POST,
                request,
                Order.class
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Error creating order", e);
            throw new RuntimeException("Failed to create order: " + e.getMessage(), e);
        }
    }

    public List<Order> getAllOrders() {
        try {
            log.info("Fetching all orders");
            ResponseEntity<String> response = restTemplate.exchange(
                config.getBaseUrl(),
                HttpMethod.GET,
                new HttpEntity<>(createHeaders()),
                String.class
            );
            return objectMapper.readValue(
                response.getBody(),
                new TypeReference<List<Order>>() {}
            );
        } catch (Exception e) {
            log.error("Error fetching all orders", e);
            throw new RuntimeException("Failed to fetch orders: " + e.getMessage(), e);
        }
    }

    public Order getOrderById(Long orderId) {
        try {
            log.info("Fetching order with ID: {}", orderId);
            ResponseEntity<Order> response = restTemplate.exchange(
                config.getBaseUrl() + "/" + orderId,
                HttpMethod.GET,
                new HttpEntity<>(createHeaders()),
                Order.class
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Error fetching order with ID: {}", orderId, e);
            throw new RuntimeException("Failed to fetch order: " + e.getMessage(), e);
        }
    }

    public Order updateOrder(Long orderId, Order order) {
        try {
            log.info("Updating order with ID: {}", orderId);
            HttpEntity<Order> request = new HttpEntity<>(order, createHeaders());
            ResponseEntity<Order> response = restTemplate.exchange(
                config.getBaseUrl() + "/" + orderId,
                HttpMethod.PUT,
                request,
                Order.class
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Error updating order with ID: {}", orderId, e);
            throw new RuntimeException("Failed to update order: " + e.getMessage(), e);
        }
    }

    public void cancelOrder(Long orderId) {
        try {
            log.info("Cancelling order with ID: {}", orderId);
            restTemplate.exchange(
                config.getBaseUrl() + "/" + orderId,
                HttpMethod.DELETE,
                new HttpEntity<>(createHeaders()),
                Void.class
            );
        } catch (Exception e) {
            log.error("Error cancelling order with ID: {}", orderId, e);
            throw new RuntimeException("Failed to cancel order: " + e.getMessage(), e);
        }
    }

    public Order updateOrderAddress(Long orderId, String address) {
        try {
            log.info("Updating address for order ID: {} to: {}", orderId, address);
            String url = UriComponentsBuilder.fromHttpUrl(config.getBaseUrl())
                    .path("/{id}/address")
                    .queryParam("address", address)
                    .buildAndExpand(orderId)
                    .toUriString();

            log.info("Built URL: {}", url);

            ResponseEntity<Order> response = restTemplate.exchange(
                url,
                HttpMethod.PATCH,
                new HttpEntity<>(createHeaders()),
                Order.class
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Error updating address for order ID: {}", orderId, e);
            throw new RuntimeException("Failed to update order address: " + e.getMessage(), e);
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
