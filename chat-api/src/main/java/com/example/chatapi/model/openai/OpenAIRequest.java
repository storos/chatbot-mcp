package com.example.chatapi.model.openai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenAIRequest {

    private String model;
    private List<Message> messages;
    private List<Function> functions;

    @JsonProperty("function_call")
    private String functionCall; // "auto" or specific function name

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String role; // "system", "user", "assistant", "function"
        private String content;
        private String name; // for function role

        @JsonProperty("function_call")
        private FunctionCall functionCall;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FunctionCall {
        private String name;
        private String arguments; // JSON string
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Function {
        private String name;
        private String description;
        private Parameters parameters;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Parameters {
        private String type;
        private Object properties;
        private List<String> required;
    }
}
