package com.example.chatapi.service;

import com.example.chatapi.model.ChatResponse;
import com.example.chatapi.model.FunctionCallInfo;
import com.example.chatapi.model.openai.OpenAIRequest;
import com.example.chatapi.model.openai.OpenAIResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAIService {

    private final WebClient.Builder webClientBuilder;
    private final McpClientService mcpClientService;
    private final ObjectMapper objectMapper;
    private final ConversationHistoryService conversationHistoryService;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${openai.model:gpt-4}")
    private String model;

    private static final String SYSTEM_PROMPT_BASE = """
            Sen bir e-ticaret müşteri destek asistanısın. Müşterilere siparişleriyle ilgili yardımcı oluyorsun.

            ÖNEMLİ KURALLAR:
            1. Bir fonksiyon çağırmadan önce, o fonksiyonun gerektirdiği TÜM parametrelerin değerlerini kullanıcıdan topla.
            2. Eğer kullanıcı gerekli bir parametreyi vermemişse, ASLA tahminde bulunma - açıkça kullanıcıya sor.
            3. Birden fazla seçenek varsa (örneğin birden fazla sipariş), MUTLAKA kullanıcıya hangisini kastettiğini sor.
            4. Kullanıcı belirsiz bir istek yaptığında (örneğin "siparişimi iptal et" ama hangi sipariş belli değil):
               a) Önce ilgili bilgileri listele (örneğin tüm siparişleri göster)
               b) Sonra kullanıcıdan spesifik seçim yapmasını iste
            5. Parametreler tamamlandıktan sonra fonksiyonu çağır - eksik parametre ile ASLA çağırma.

            Müşteriye her zaman nazik ve yardımsever ol. Türkçe yanıt ver.
            """;

    // Thread-local storage for tracking function calls
    private static final ThreadLocal<List<FunctionCallInfo>> functionCallsTracker = ThreadLocal.withInitial(ArrayList::new);

    /**
     * MCP tool'larından dinamik olarak system prompt oluşturur
     */
    private String buildSystemPrompt() {
        List<Map<String, Object>> tools = mcpClientService.getAvailableTools();

        StringBuilder capabilities = new StringBuilder();
        capabilities.append("\n\nYapabileceklerin:\n");

        for (Map<String, Object> tool : tools) {
            String description = (String) tool.get("description");
            capabilities.append("- ").append(description).append("\n");
        }

        return SYSTEM_PROMPT_BASE + capabilities.toString();
    }

    /**
     * Kullanıcı mesajını OpenAI'ye gönderir ve yanıt alır
     * Gerekirse MCP fonksiyonlarını çağırır
     */
    public ChatResponse chat(String userMessage, String sessionId) {
        // Clear previous function calls
        functionCallsTracker.get().clear();

        try {
            // Dinamik system prompt oluştur
            String systemPrompt = buildSystemPrompt();

            // Session için conversation history'yi başlat (yoksa)
            conversationHistoryService.initializeSession(sessionId, systemPrompt);

            // MCP araçlarını al ve OpenAI fonksiyonlarına dönüştür
            List<OpenAIRequest.Function> functions = buildFunctionsFromMcpTools();

            // Kullanıcı mesajını history'e ekle
            OpenAIRequest.Message userMessageObj = OpenAIRequest.Message.builder()
                    .role("user")
                    .content(userMessage)
                    .build();
            conversationHistoryService.addMessage(sessionId, userMessageObj);

            // Mevcut conversation history'yi al
            List<OpenAIRequest.Message> messages = new ArrayList<>(conversationHistoryService.getHistory(sessionId));

            log.info("Session {}: Sending {} messages to OpenAI", sessionId, messages.size());

            OpenAIRequest request = OpenAIRequest.builder()
                    .model(model)
                    .messages(messages)
                    .functions(functions)
                    .functionCall("auto")
                    .build();

            // OpenAI'ye istek gönder
            OpenAIResponse response = callOpenAI(request);

            // Yanıtı işle ve history'e ekle
            String responseText = processResponse(response, sessionId);

            // Get the list of called functions
            List<FunctionCallInfo> calledFunctions = new ArrayList<>(functionCallsTracker.get());

            return ChatResponse.builder()
                    .response(responseText)
                    .sessionId(sessionId)
                    .functionsCalled(calledFunctions)
                    .build();

        } catch (Exception e) {
            log.error("Error in chat", e);
            return ChatResponse.builder()
                    .response("Üzgünüm, bir hata oluştu. Lütfen daha sonra tekrar deneyin.")
                    .sessionId(sessionId)
                    .functionsCalled(new ArrayList<>())
                    .build();
        } finally {
            // Clean up thread-local
            functionCallsTracker.remove();
        }
    }

    private OpenAIResponse callOpenAI(OpenAIRequest request) {
        WebClient webClient = webClientBuilder.build();

        log.info("Calling OpenAI API: model={}, functions={}", request.getModel(),
                request.getFunctions() != null ? request.getFunctions().size() : 0);

        return webClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OpenAIResponse.class)
                .block();
    }

    private String processResponse(OpenAIResponse response, String sessionId) {
        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            return "Üzgünüm, yanıt alamadım.";
        }

        OpenAIResponse.Choice choice = response.getChoices().get(0);
        OpenAIRequest.Message assistantMessage = choice.getMessage();

        // Eğer fonksiyon çağrısı varsa
        if ("function_call".equals(choice.getFinishReason()) && assistantMessage.getFunctionCall() != null) {
            log.info("Function call detected: {}", assistantMessage.getFunctionCall().getName());
            return handleFunctionCall(assistantMessage, sessionId);
        }

        // Normal mesaj yanıtı - assistant response'u history'e ekle
        conversationHistoryService.addMessage(sessionId, assistantMessage);
        return assistantMessage.getContent();
    }

    private String handleFunctionCall(OpenAIRequest.Message assistantMessage, String sessionId) {
        try {
            String functionName = assistantMessage.getFunctionCall().getName();
            String argumentsJson = assistantMessage.getFunctionCall().getArguments();

            log.info("Calling function: {} with arguments: {}", functionName, argumentsJson);

            // Argümanları parse et
            Map<String, Object> arguments = objectMapper.readValue(argumentsJson, Map.class);

            // MCP fonksiyonunu çağır
            String functionResult = mcpClientService.callFunction(functionName, arguments);

            log.info("Function result: {}", functionResult);

            // Parse function result to Object
            Object parsedResponse;
            try {
                parsedResponse = objectMapper.readValue(functionResult, Object.class);
            } catch (Exception e) {
                // If parsing fails, use the raw string
                parsedResponse = functionResult;
            }

            // Track this function call with request and response
            FunctionCallInfo callInfo = FunctionCallInfo.builder()
                    .functionName(functionName)
                    .request(arguments)
                    .response(parsedResponse)
                    .build();
            functionCallsTracker.get().add(callInfo);

            // Konuşma geçmişine ekle (assistant'ın function call mesajı)
            conversationHistoryService.addMessage(sessionId, assistantMessage);

            // Function result mesajını ekle
            OpenAIRequest.Message functionResultMessage = OpenAIRequest.Message.builder()
                    .role("function")
                    .name(functionName)
                    .content(functionResult)
                    .build();
            conversationHistoryService.addMessage(sessionId, functionResultMessage);

            // Güncel history'yi al
            List<OpenAIRequest.Message> updatedHistory = new ArrayList<>(conversationHistoryService.getHistory(sessionId));

            // OpenAI'ye tekrar gönder (fonksiyon sonucuyla)
            OpenAIRequest followUpRequest = OpenAIRequest.builder()
                    .model(model)
                    .messages(updatedHistory)
                    .build();

            OpenAIResponse followUpResponse = callOpenAI(followUpRequest);

            // Final yanıtı al ve history'e ekle
            if (followUpResponse != null && !followUpResponse.getChoices().isEmpty()) {
                OpenAIRequest.Message finalResponse = followUpResponse.getChoices().get(0).getMessage();
                conversationHistoryService.addMessage(sessionId, finalResponse);
                return finalResponse.getContent();
            }

            return "Fonksiyon başarıyla çalıştı.";

        } catch (Exception e) {
            log.error("Error handling function call", e);
            return "Fonksiyon çağrısında bir hata oluştu: " + e.getMessage();
        }
    }

    /**
     * MCP araçlarını OpenAI fonksiyon formatına dönüştürür
     * Tool definitions'dan gelen inputSchema'yı direkt kullanır
     */
    private List<OpenAIRequest.Function> buildFunctionsFromMcpTools() {
        List<Map<String, Object>> mcpTools = mcpClientService.getAvailableTools();
        List<OpenAIRequest.Function> functions = new ArrayList<>();

        for (Map<String, Object> tool : mcpTools) {
            String name = (String) tool.get("name");
            String description = (String) tool.get("description");
            Map<String, Object> inputSchema = (Map<String, Object>) tool.get("inputSchema");

            // InputSchema'yı OpenAI Parameters formatına dönüştür
            OpenAIRequest.Parameters parameters = convertInputSchemaToParameters(inputSchema);

            functions.add(OpenAIRequest.Function.builder()
                    .name(name)
                    .description(description)
                    .parameters(parameters)
                    .build());
        }

        log.info("Built {} OpenAI functions from MCP tools", functions.size());
        return functions;
    }

    /**
     * MCP inputSchema'yı OpenAI Parameters formatına dönüştürür
     */
    private OpenAIRequest.Parameters convertInputSchemaToParameters(Map<String, Object> inputSchema) {
        if (inputSchema == null) {
            // Parametresiz fonksiyon
            return OpenAIRequest.Parameters.builder()
                    .type("object")
                    .properties(new HashMap<>())
                    .required(new ArrayList<>())
                    .build();
        }

        String type = (String) inputSchema.getOrDefault("type", "object");
        Map<String, Object> properties = (Map<String, Object>) inputSchema.getOrDefault("properties", new HashMap<>());
        List<String> required = (List<String>) inputSchema.getOrDefault("required", new ArrayList<>());

        return OpenAIRequest.Parameters.builder()
                .type(type)
                .properties(properties)
                .required(required)
                .build();
    }
}
