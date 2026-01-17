package com.example.orderapi.controller;

import com.example.orderapi.dto.HealthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/health")
@Tag(name = "Health Check", description = "APIs for service health monitoring")
public class HealthController {

    @Value("${spring.application.name:order-api}")
    private String applicationName;

    @Value("${project.version:1.0.0}")
    private String version;

    @Operation(
            summary = "Health check endpoint",
            description = "Returns the service status and version information. Used for monitoring and availability checks."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Service is healthy",
                    content = @Content(schema = @Schema(implementation = HealthResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<HealthResponse> health() {
        log.debug("GET /health - Health check request");

        HealthResponse response = new HealthResponse(
                "UP",
                version,
                LocalDateTime.now()
        );

        log.debug("GET /health - Response: {}", response);
        return ResponseEntity.ok(response);
    }
}
