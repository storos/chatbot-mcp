package com.example.orderapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Health check response")
public class HealthResponse {

    @Schema(description = "Service status", example = "UP")
    private String status;

    @Schema(description = "Service version", example = "1.0.0")
    private String version;

    @Schema(description = "Current timestamp")
    private LocalDateTime timestamp;
}
