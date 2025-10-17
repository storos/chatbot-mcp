package com.example.chatapi.controller;

import com.example.chatapi.model.ChatRequest;
import com.example.chatapi.model.ChatResponse;
import com.example.chatapi.service.OpenAIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final OpenAIService openAIService;

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        try {
            log.info("Chat request received: message='{}', sessionId='{}'",
                    request.getMessage(), request.getSessionId());

            // Session ID yoksa oluştur
            String sessionId = request.getSessionId() != null ?
                    request.getSessionId() : UUID.randomUUID().toString();

            // OpenAI ile işle - artık ChatResponse döndürüyor
            ChatResponse chatResponse = openAIService.chat(request.getMessage(), sessionId);

            log.info("Chat response: functionsCalled={}", chatResponse.getFunctionsCalled());

            return ResponseEntity.ok(chatResponse);

        } catch (Exception e) {
            log.error("Error processing chat request", e);

            ChatResponse errorResponse = ChatResponse.builder()
                    .response("Üzgünüm, bir hata oluştu. Lütfen daha sonra tekrar deneyin.")
                    .sessionId(request.getSessionId())
                    .functionsCalled(java.util.Collections.emptyList())
                    .build();

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Chat API is running");
    }
}
