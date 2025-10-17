package com.example.chatapi.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class McpClientService {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${mcp.server.url}")
    private String mcpServerUrl;

    // Cache for tool definitions
    private List<Map<String, Object>> cachedTools = null;

    /**
     * MCP sunucusundan mevcut araçları (tools) getirir
     * İlk çağrıda cache'ler, sonraki çağrılarda cache'ten döner
     */
    public List<Map<String, Object>> getAvailableTools() {
        if (cachedTools != null) {
            return cachedTools;
        }

        try {
            log.info("Fetching available MCP tools from: {}", mcpServerUrl);

            WebClient webClient = webClientBuilder.baseUrl(mcpServerUrl).build();

            Map<String, Object> response = webClient.get()
                    .uri("/mcp/tools")
                    .retrieve()
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            if (response != null && response.containsKey("tools")) {
                cachedTools = (List<Map<String, Object>>) response.get("tools");
                log.info("Found and cached {} MCP tools", cachedTools.size());
                return cachedTools;
            }

            return List.of();
        } catch (Exception e) {
            log.error("Error fetching MCP tools", e);
            return List.of();
        }
    }

    /**
     * MCP fonksiyonunu dinamik olarak çağırır
     * Tool definitions'dan endpoint ve method bilgisini alıp çağrıyı yapar
     *
     * @param functionName Fonksiyon adı (örn: "get_all_orders", "get_order_by_id")
     * @param arguments Fonksiyon parametreleri
     * @return Fonksiyon sonucu
     */
    public String callFunction(String functionName, Map<String, Object> arguments) {
        try {
            log.info("Calling MCP function: {} with arguments: {}", functionName, arguments);

            // Tool definition'ı bul
            Map<String, Object> toolDef = findToolDefinition(functionName);
            if (toolDef == null) {
                log.warn("Unknown function: {}", functionName);
                return "{\"error\": \"Unknown function: " + functionName + "\"}";
            }

            String endpoint = (String) toolDef.get("endpoint");
            String method = (String) toolDef.get("method");

            // Path parametrelerini değiştir (örn: /mcp/orders/{id} -> /mcp/orders/123)
            String uri = replacePlaceholders(endpoint, arguments);

            // Query parametrelerini ekle (PATCH için address gibi)
            Map<String, Object> queryParams = extractQueryParams(arguments, endpoint);

            WebClient webClient = webClientBuilder.baseUrl(mcpServerUrl).build();

            // HTTP metoduna göre çağrı yap
            String response = executeRequest(webClient, method, uri, queryParams);

            log.info("MCP function {} executed successfully", functionName);
            return response;

        } catch (Exception e) {
            log.error("Error calling MCP function: {}", functionName, e);
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    /**
     * Tool definition'ı bul
     */
    private Map<String, Object> findToolDefinition(String functionName) {
        List<Map<String, Object>> tools = getAvailableTools();
        for (Map<String, Object> tool : tools) {
            if (functionName.equals(tool.get("name"))) {
                return tool;
            }
        }
        return null;
    }

    /**
     * Endpoint içindeki placeholder'ları değiştir
     * Örn: /mcp/orders/{id} + {orderId: 123} -> /mcp/orders/123
     */
    private String replacePlaceholders(String endpoint, Map<String, Object> arguments) {
        String result = endpoint;

        // {id} gibi placeholder'ları bul ve değiştir
        if (result.contains("{id}") && arguments.containsKey("orderId")) {
            result = result.replace("{id}", String.valueOf(arguments.get("orderId")));
        }

        return result;
    }

    /**
     * Query parametrelerini çıkar (path parametresi olmayanlar)
     */
    private Map<String, Object> extractQueryParams(Map<String, Object> arguments, String endpoint) {
        Map<String, Object> queryParams = new HashMap<>();

        for (Map.Entry<String, Object> entry : arguments.entrySet()) {
            String key = entry.getKey();
            // orderId path parametresi olduğu için query'ye ekleme
            if (!"orderId".equals(key)) {
                queryParams.put(key, entry.getValue());
            }
        }

        return queryParams;
    }

    /**
     * HTTP isteğini yürüt
     */
    private String executeRequest(WebClient webClient, String method, String uri, Map<String, Object> queryParams) {
        WebClient.RequestHeadersSpec<?> spec;

        switch (method.toUpperCase()) {
            case "GET":
                spec = webClient.get().uri(uriBuilder -> {
                    uriBuilder.path(uri);
                    queryParams.forEach((key, value) -> uriBuilder.queryParam(key, value));
                    return uriBuilder.build(false); // false = encode
                });
                break;

            case "POST":
                spec = webClient.post().uri(uri);
                break;

            case "PUT":
                spec = webClient.put().uri(uri);
                break;

            case "DELETE":
                spec = webClient.delete().uri(uri);
                break;

            case "PATCH":
                spec = webClient.patch().uri(uriBuilder -> {
                    uriBuilder.path(uri);
                    queryParams.forEach((key, value) -> uriBuilder.queryParam(key, value));
                    return uriBuilder.build(false); // false = encode
                });
                break;

            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }

        return spec.retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private Long getLongValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            return Long.parseLong((String) value);
        }
        return null;
    }
}
