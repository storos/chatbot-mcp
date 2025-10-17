package com.example.orderapimcp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan
public class OrderApiMcpApplication {

    public static void main(String[] args) {
        log.info("Starting Order API MCP Server...");
        SpringApplication.run(OrderApiMcpApplication.class, args);
        log.info("Order API MCP Server started successfully");
    }
}
