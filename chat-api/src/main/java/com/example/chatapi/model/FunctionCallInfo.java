package com.example.chatapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FunctionCallInfo {

    private String functionName;
    private Object request;
    private Object response;
}
