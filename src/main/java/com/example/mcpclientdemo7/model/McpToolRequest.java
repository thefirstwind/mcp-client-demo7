package com.example.mcpclientdemo7.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class McpToolRequest {
    private String tool;
    private Map<String, Object> parameters;
    private String requestId;
    
    @Override
    public String toString() {
        return "McpToolRequest{" +
                "tool='" + tool + '\'' +
                ", parameters=" + parameters +
                ", requestId='" + requestId + '\'' +
                '}';
    }
} 