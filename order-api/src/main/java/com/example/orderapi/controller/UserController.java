package com.example.orderapi.controller;

import com.example.orderapi.model.UserProfile;
import com.example.orderapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing user profiles")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Get user profile by ID",
            description = "Retrieves user profile information including name, email, and registration date."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User profile found",
                    content = @Content(schema = @Schema(implementation = UserProfile.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            )
    })
    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfile> getUserProfile(
            @Parameter(description = "ID of the user to retrieve profile for", required = true)
            @PathVariable Long userId) {
        log.info("GET /api/users/{}/profile - Request", userId);
        UserProfile profile = userService.getUserProfile(userId);
        log.info("GET /api/users/{}/profile - Response: {}", userId, profile);
        return ResponseEntity.ok(profile);
    }
}
