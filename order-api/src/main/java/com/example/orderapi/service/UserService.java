package com.example.orderapi.service;

import com.example.orderapi.exception.UserNotFoundException;
import com.example.orderapi.model.UserProfile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    /**
     * Get user profile by user ID - returns dummy profile data
     */
    public UserProfile getUserProfile(Long userId) {
        if (userId == null || userId <= 0) {
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }

        // Return dummy user profile
        return UserProfile.builder()
                .userId(userId)
                .name("John Doe")
                .email("john.doe@example.com")
                .registrationDate(LocalDateTime.now().minusMonths(6))
                .build();
    }
}
