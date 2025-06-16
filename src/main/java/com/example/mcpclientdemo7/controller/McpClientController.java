package com.example.mcpclientdemo7.controller;

import com.example.mcpclientdemo7.service.McpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.*;

@RestController
@RequestMapping("/api/mcp")
@Slf4j
public class McpClientController {

    @Autowired
    private McpService mcpService;

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        return response;
    }

    /**
     * Synchronous call to UserCenter using WebClient
     */
    @PostMapping("/sync/userCenter/webclient")
    public ResponseEntity<Object> callUserCenterSyncWebClient(@RequestBody Map<String, Object> request) {
        String toolName = "sayHello";
        String response = mcpService.callUserCenterSync(toolName, request);
        
        if (response.startsWith("Error:")) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", response.substring(7));
            errorResponse.put("status", "error");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("response", response);
        successResponse.put("status", "success");
        return ResponseEntity.ok(successResponse);
    }

    /**
     * Synchronous call to UserQKCenter using WebClient
     */
    @PostMapping("/sync/userQKCenter/webclient")
    public ResponseEntity<Object> callUserQKCenterSyncWebClient(@RequestBody Map<String, Object> request) {
        String toolName = "sayHello";
        String response = mcpService.callUserQKCenterSync(toolName, request);
        
        if (response.startsWith("Error:")) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", response.substring(7));
            errorResponse.put("status", "error");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("response", response);
        successResponse.put("status", "success");
        return ResponseEntity.ok(successResponse);
    }

    /**
     * Asynchronous call to UserCenter using WebClient
     */
    @PostMapping("/async/userCenter/webclient")
    public Mono<ResponseEntity<Object>> callUserCenterAsyncWebClient(@RequestBody Map<String, Object> request) {
        String toolName = "sayHello";
        return mcpService.callUserCenterAsync(toolName, request)
            .map(response -> {
                if (response.startsWith("Error:")) {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("error", response.substring(7));
                    errorResponse.put("status", "error");
                    return ResponseEntity.badRequest().body(errorResponse);
                }
                
                Map<String, Object> successResponse = new HashMap<>();
                successResponse.put("response", response);
                successResponse.put("status", "success");
                return ResponseEntity.ok(successResponse);
            });
    }

    /**
     * Asynchronous call to UserQKCenter using WebClient
     */
    @PostMapping("/async/userQKCenter/webclient")
    public Mono<ResponseEntity<Object>> callUserQKCenterAsyncWebClient(@RequestBody Map<String, Object> request) {
        String toolName = "sayHello";
        return mcpService.callUserQKCenterAsync(toolName, request)
            .map(response -> {
                if (response.startsWith("Error:")) {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("error", response.substring(7));
                    errorResponse.put("status", "error");
                    return ResponseEntity.badRequest().body(errorResponse);
                }
                
                Map<String, Object> successResponse = new HashMap<>();
                successResponse.put("response", response);
                successResponse.put("status", "success");
                return ResponseEntity.ok(successResponse);
            });
    }

    /**
     * Simple GET endpoints for testing
     */
    @GetMapping("/say-hello/userCenter")
    public ResponseEntity<Object> sayHelloUserCenter() {
        Map<String, Object> request = new HashMap<>();
        request.put("message", "Hello from MCP client!");
        String toolName = "sayHello";
        String response = mcpService.callUserCenterSync(toolName, request);
        
        if (response.startsWith("Error:")) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", response.substring(7));
            errorResponse.put("status", "error");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("response", response);
        successResponse.put("status", "success");
        return ResponseEntity.ok(successResponse);
    }

    @GetMapping("/say-hello/userQKCenter")
    public ResponseEntity<Object> sayHelloUserQKCenter() {
        Map<String, Object> request = new HashMap<>();
        request.put("message", "Hello from MCP client!");
        String toolName = "sayHello";
        String response = mcpService.callUserQKCenterSync(toolName, request);
        
        if (response.startsWith("Error:")) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", response.substring(7));
            errorResponse.put("status", "error");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("response", response);
        successResponse.put("status", "success");
        return ResponseEntity.ok(successResponse);
    }
} 