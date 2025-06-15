package com.example.mcpclientdemo7.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class McpToolResponse {
    private String requestId;
    private String result;
    private String error;
    
    @Override
    public String toString() {
        return "McpToolResponse{" +
                "requestId='" + requestId + '\'' +
                ", result='" + result + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
} 