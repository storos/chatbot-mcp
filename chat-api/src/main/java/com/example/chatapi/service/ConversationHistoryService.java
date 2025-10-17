package com.example.chatapi.service;

import com.example.chatapi.model.openai.OpenAIRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ConversationHistoryService {

    // Session ID -> Conversation History
    private final Map<String, List<OpenAIRequest.Message>> conversationStore = new ConcurrentHashMap<>();

    /**
     * Yeni bir session için conversation başlatır
     */
    public void initializeSession(String sessionId, String systemPrompt) {
        if (!conversationStore.containsKey(sessionId)) {
            List<OpenAIRequest.Message> messages = new ArrayList<>();
            messages.add(OpenAIRequest.Message.builder()
                    .role("system")
                    .content(systemPrompt)
                    .build());
            conversationStore.put(sessionId, messages);
            log.info("Initialized conversation for session: {}", sessionId);
        }
    }

    /**
     * Session için conversation history'yi döndürür
     */
    public List<OpenAIRequest.Message> getHistory(String sessionId) {
        return conversationStore.getOrDefault(sessionId, new ArrayList<>());
    }

    /**
     * Session'a yeni mesaj ekler
     */
    public void addMessage(String sessionId, OpenAIRequest.Message message) {
        List<OpenAIRequest.Message> history = conversationStore.computeIfAbsent(sessionId, k -> new ArrayList<>());
        history.add(message);
        log.debug("Added message to session {}: role={}", sessionId, message.getRole());
    }

    /**
     * Session'ı temizler
     */
    public void clearSession(String sessionId) {
        conversationStore.remove(sessionId);
        log.info("Cleared conversation for session: {}", sessionId);
    }

    /**
     * Tüm session'ları temizler
     */
    public void clearAll() {
        conversationStore.clear();
        log.info("Cleared all conversations");
    }

    /**
     * Aktif session sayısını döndürür
     */
    public int getActiveSessionCount() {
        return conversationStore.size();
    }

    /**
     * Session'ın var olup olmadığını kontrol eder
     */
    public boolean hasSession(String sessionId) {
        return conversationStore.containsKey(sessionId);
    }

    /**
     * Session için mesaj sayısını döndürür
     */
    public int getMessageCount(String sessionId) {
        return conversationStore.getOrDefault(sessionId, new ArrayList<>()).size();
    }
}
