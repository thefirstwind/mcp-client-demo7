package com.example.mcpclientdemo7.service;

import com.example.mcpclientdemo7.model.McpToolRequest;
import com.example.mcpclientdemo7.model.McpToolResponse;
import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;
import jakarta.annotation.PostConstruct;

@Service
@Slf4j
public class McpService {

    private final McpSyncClient userCenterMcpClient;
    private final McpSyncClient userQKCenterMcpClient;
    private final McpAsyncClient userCenterMcpAsyncClient;
    private final McpAsyncClient userQKCenterMcpAsyncClient;
    
    // Flags to track if the servers support tools capability
    private boolean userCenterSupportsTools = false;
    private boolean userQKCenterSupportsTools = false;

    @Value("${mcp.server.usercenter.sse-path:#{null}}")
    private String userCenterSsePath;

    @Value("${mcp.server.userqkcenter.sse-path:#{null}}")
    private String userQKCenterSsePath;

    public McpService(
            @Qualifier("userCenterMcpClient") McpSyncClient userCenterMcpClient,
            @Qualifier("userQKCenterMcpClient") McpSyncClient userQKCenterMcpClient,
            @Qualifier("userCenterMcpAsyncClient") McpAsyncClient userCenterMcpAsyncClient,
            @Qualifier("userQKCenterMcpAsyncClient") McpAsyncClient userQKCenterMcpAsyncClient) {
        this.userCenterMcpClient = userCenterMcpClient;
        this.userQKCenterMcpClient = userQKCenterMcpClient;
        this.userCenterMcpAsyncClient = userCenterMcpAsyncClient;
        this.userQKCenterMcpAsyncClient = userQKCenterMcpAsyncClient;
    }
    
    @PostConstruct
    public void initializeClients() {
        initializeUserCenterSyncClient();
        initializeUserQKCenterSyncClient();
        initializeUserCenterAsyncClient();
        initializeUserQKCenterAsyncClient();
    }
    
    private void initializeUserCenterSyncClient() {
        try {
            log.info("Initializing UserCenter MCP sync client...");
            userCenterMcpClient.initialize();
            
            // Check if the server supports tools capability
            McpSchema.ServerCapabilities capabilities = userCenterMcpClient.getServerCapabilities();
            if (capabilities != null && capabilities.tools() != null) {
                userCenterSupportsTools = true;
                log.info("UserCenter MCP server supports tools capability");
            } else {
                userCenterSupportsTools = false;
                log.warn("UserCenter MCP server does not support tools capability");
            }
            
            log.info("UserCenter MCP sync client initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize UserCenter MCP sync client: {}", e.getMessage(), e);
        }
    }
    
    private void initializeUserQKCenterSyncClient() {
        try {
            log.info("Initializing UserQKCenter MCP sync client...");
            userQKCenterMcpClient.initialize();
            
            // Check if the server supports tools capability
            McpSchema.ServerCapabilities capabilities = userQKCenterMcpClient.getServerCapabilities();
            if (capabilities != null && capabilities.tools() != null) {
                userQKCenterSupportsTools = true;
                log.info("UserQKCenter MCP server supports tools capability");
            } else {
                userQKCenterSupportsTools = false;
                log.warn("UserQKCenter MCP server does not support tools capability");
            }
            
            log.info("UserQKCenter MCP sync client initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize UserQKCenter MCP sync client: {}", e.getMessage(), e);
        }
    }
    
    private void initializeUserCenterAsyncClient() {
        try {
            log.info("Initializing UserCenter MCP async client...");
            userCenterMcpAsyncClient.initialize()
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                    .doBeforeRetry(retrySignal -> 
                        log.warn("Retrying UserCenter async client initialization, attempt: {}", 
                            retrySignal.totalRetries() + 1)))
                .doOnError(e -> log.error("Failed to initialize UserCenter MCP async client after retries: {}", e.getMessage(), e))
                .block(Duration.ofSeconds(30));
            log.info("UserCenter MCP async client initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize UserCenter MCP async client: {}", e.getMessage(), e);
        }
    }
    
    private void initializeUserQKCenterAsyncClient() {
        try {
            log.info("Initializing UserQKCenter MCP async client...");
            userQKCenterMcpAsyncClient.initialize()
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                    .doBeforeRetry(retrySignal -> 
                        log.warn("Retrying UserQKCenter async client initialization, attempt: {}", 
                            retrySignal.totalRetries() + 1)))
                .doOnError(e -> log.error("Failed to initialize UserQKCenter MCP async client after retries: {}", e.getMessage(), e))
                .block(Duration.ofSeconds(30));
            log.info("UserQKCenter MCP async client initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize UserQKCenter MCP async client: {}", e.getMessage(), e);
        }
    }

    /**
     * Synchronous call to UserCenter MCP server
     */
    public String callUserCenterSync(String toolName, Map<String, Object> parameters) {
        try {
            // Check if tools capability is supported
            if (!userCenterSupportsTools) {
                return "Error: Server does not provide tools capability. Using direct API call instead.";
            }
            
            McpSchema.CallToolRequest request = new McpSchema.CallToolRequest(toolName, parameters);
            log.debug("Calling UserCenter with request: {}", request);
            
            McpSchema.CallToolResult result = userCenterMcpClient.callTool(request);
            
            if (result.isError() != null && result.isError()) {
                return "Error: " + result.content();
            } else {
                return String.valueOf(result.content());
            }
        } catch (McpError e) {
            if (e.getMessage().contains("Server does not provide tools capability")) {
                log.warn("UserCenter MCP server does not support tools capability. Using fallback method.");
                return handleFallbackApiCall("userCenter", toolName, parameters);
            } else {
                log.error("Error calling UserCenter MCP server", e);
                return "Error: " + e.getMessage();
            }
        } catch (Exception e) {
            log.error("Error calling UserCenter MCP server", e);
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Synchronous call to UserQKCenter MCP server
     */
    public String callUserQKCenterSync(String toolName, Map<String, Object> parameters) {
        try {
            // Check if tools capability is supported
            if (!userQKCenterSupportsTools) {
                return "Error: Server does not provide tools capability. Using direct API call instead.";
            }
            
            McpSchema.CallToolRequest request = new McpSchema.CallToolRequest(toolName, parameters);
            log.debug("Calling UserQKCenter with request: {}", request);
            
            McpSchema.CallToolResult result = userQKCenterMcpClient.callTool(request);
            
            if (result.isError() != null && result.isError()) {
                return "Error: " + result.content();
            } else {
                return String.valueOf(result.content());
            }
        } catch (McpError e) {
            if (e.getMessage().contains("Server does not provide tools capability")) {
                log.warn("UserQKCenter MCP server does not support tools capability. Using fallback method.");
                return handleFallbackApiCall("userQKCenter", toolName, parameters);
            } else {
                log.error("Error calling UserQKCenter MCP server", e);
                return "Error: " + e.getMessage();
            }
        } catch (Exception e) {
            log.error("Error calling UserQKCenter MCP server", e);
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Asynchronous call to UserCenter MCP server
     */
    public Mono<String> callUserCenterAsync(String toolName, Map<String, Object> parameters) {
        // Check if tools capability is supported
        if (!userCenterSupportsTools) {
            return Mono.just("Error: Server does not provide tools capability. Using direct API call instead.");
        }
        
        McpSchema.CallToolRequest request = new McpSchema.CallToolRequest(toolName, parameters);
        log.debug("Calling UserCenter asynchronously with request: {}", request);
        
        return userCenterMcpAsyncClient.callTool(request)
            .retryWhen(Retry.backoff(2, Duration.ofSeconds(1))
                .doBeforeRetry(retrySignal -> 
                    log.warn("Retrying UserCenter async call, attempt: {}", 
                        retrySignal.totalRetries() + 1)))
            .map(result -> {
                log.debug("Received async response from UserCenter: {}", result);
                if (result.isError() != null && result.isError()) {
                    return "Error: " + result.content();
                } else {
                    return String.valueOf(result.content());
                }
            })
            .onErrorResume(e -> {
                if (e instanceof McpError && e.getMessage().contains("Server does not provide tools capability")) {
                    log.warn("UserCenter MCP server does not support tools capability. Using fallback method.");
                    return Mono.just(handleFallbackApiCall("userCenter", toolName, parameters));
                } else {
                    log.error("Error calling UserCenter MCP server asynchronously: {}", e.getMessage(), e);
                    return Mono.just("Error: " + e.getMessage());
                }
            });
    }

    /**
     * Asynchronous call to UserQKCenter MCP server
     */
    public Mono<String> callUserQKCenterAsync(String toolName, Map<String, Object> parameters) {
        // Check if tools capability is supported
        if (!userQKCenterSupportsTools) {
            return Mono.just("Error: Server does not provide tools capability. Using direct API call instead.");
        }
        
        McpSchema.CallToolRequest request = new McpSchema.CallToolRequest(toolName, parameters);
        log.debug("Calling UserQKCenter asynchronously with request: {}", request);
        
        return userQKCenterMcpAsyncClient.callTool(request)
            .retryWhen(Retry.backoff(2, Duration.ofSeconds(1))
                .doBeforeRetry(retrySignal -> 
                    log.warn("Retrying UserQKCenter async call, attempt: {}", 
                        retrySignal.totalRetries() + 1)))
            .map(result -> {
                log.debug("Received async response from UserQKCenter: {}", result);
                if (result.isError() != null && result.isError()) {
                    return "Error: " + result.content();
                } else {
                    return String.valueOf(result.content());
                }
            })
            .onErrorResume(e -> {
                if (e instanceof McpError && e.getMessage().contains("Server does not provide tools capability")) {
                    log.warn("UserQKCenter MCP server does not support tools capability. Using fallback method.");
                    return Mono.just(handleFallbackApiCall("userQKCenter", toolName, parameters));
                } else {
                    log.error("Error calling UserQKCenter MCP server asynchronously: {}", e.getMessage(), e);
                    return Mono.just("Error: " + e.getMessage());
                }
            });
    }
    
    /**
     * Fallback method to handle direct API calls when tools capability is not available
     */
    private String handleFallbackApiCall(String server, String toolName, Map<String, Object> parameters) {
        // This is a fallback method that would normally implement direct API calls
        // For now, we'll just return a message indicating that a direct API call would be made
        Map<String, Object> response = new HashMap<>();
        response.put("server", server);
        response.put("toolName", toolName);
        response.put("parameters", parameters);
        response.put("message", "This is a fallback response since the server does not support tools capability");
        
        return response.toString();
    }
} 