package com.example.orderapimcp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "order.api")
public class OrderApiConfig {
    private String baseUrl = "http://localhost:8080/api/orders";
}
