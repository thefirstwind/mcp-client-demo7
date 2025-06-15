import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.WebFluxSseClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class McpConnectivityTest {

    public static void main(String[] args) {
        System.out.println("Testing connectivity to MCP servers...");
        
        // Test UserCenter
        testUserCenterSync();
        testUserCenterAsync();
        
        // Test UserQKCenter
        testUserQKCenterSync();
        testUserQKCenterAsync();
    }
    
    private static void testUserCenterSync() {
        System.out.println("\nTesting UserCenter Sync (http://127.0.0.1:8084)...");
        
        WebClient.Builder webClientBuilder = WebClient.builder()
                .baseUrl("http://127.0.0.1:8084")
                .defaultHeader("Accept", MediaType.TEXT_EVENT_STREAM_VALUE);
        
        ObjectMapper objectMapper = new ObjectMapper();
        WebFluxSseClientTransport transport = new WebFluxSseClientTransport(
                webClientBuilder, 
                objectMapper,
                "/sse"
        );
        
        McpSyncClient mcpSyncClient = McpClient.sync(transport)
                .clientInfo(new McpSchema.Implementation("mcp-client-demo7", "1.0.0"))
                .requestTimeout(Duration.ofSeconds(30))
                .build();
        
        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("message", "Hello from MCP Sync client!");
            
            System.out.println("Sending sync request to sayHello tool...");
            McpSchema.CallToolRequest request = new McpSchema.CallToolRequest("sayHello", parameters);
            McpSchema.CallToolResult result = mcpSyncClient.callTool(request);
            System.out.println("Sync Response: " + (result != null ? result.toString() : "null"));
        } catch (Exception e) {
            System.err.println("Failed to connect to UserCenter (Sync): " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testUserCenterAsync() {
        System.out.println("\nTesting UserCenter Async (http://127.0.0.1:8084)...");
        
        WebClient.Builder webClientBuilder = WebClient.builder()
                .baseUrl("http://127.0.0.1:8084")
                .defaultHeader("Accept", MediaType.TEXT_EVENT_STREAM_VALUE);
        
        ObjectMapper objectMapper = new ObjectMapper();
        WebFluxSseClientTransport transport = new WebFluxSseClientTransport(
                webClientBuilder,
                objectMapper,
                "/sse"
        );
        
        McpAsyncClient mcpAsyncClient = McpClient.async(transport)
                .clientInfo(new McpSchema.Implementation("mcp-client-demo7", "1.0.0"))
                .requestTimeout(Duration.ofSeconds(30))
                .build();
        
        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("message", "Hello from MCP Async client!");
            
            System.out.println("Sending async request to sayHello tool...");
            McpSchema.CallToolRequest request = new McpSchema.CallToolRequest("sayHello", parameters);
            Mono<McpSchema.CallToolResult> resultMono = mcpAsyncClient.callTool(request);
            McpSchema.CallToolResult result = resultMono.block();
            System.out.println("Async Response: " + (result != null ? result.toString() : "null"));
        } catch (Exception e) {
            System.err.println("Failed to connect to UserCenter (Async): " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testUserQKCenterSync() {
        System.out.println("\nTesting UserQKCenter Sync (http://127.0.0.1:8085)...");
        
        WebClient.Builder webClientBuilder = WebClient.builder()
                .baseUrl("http://127.0.0.1:8085")
                .defaultHeader("Accept", MediaType.TEXT_EVENT_STREAM_VALUE);
        
        ObjectMapper objectMapper = new ObjectMapper();
        WebFluxSseClientTransport transport = new WebFluxSseClientTransport(
                webClientBuilder,
                objectMapper,
                "/sse"
        );
        
        McpSyncClient mcpSyncClient = McpClient.sync(transport)
                .clientInfo(new McpSchema.Implementation("mcp-client-demo7", "1.0.0"))
                .requestTimeout(Duration.ofSeconds(30))
                .build();
        
        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("message", "Hello from MCP Sync client!");
            
            System.out.println("Sending sync request to sayHello tool...");
            McpSchema.CallToolRequest request = new McpSchema.CallToolRequest("sayHello", parameters);
            McpSchema.CallToolResult result = mcpSyncClient.callTool(request);
            System.out.println("Sync Response: " + (result != null ? result.toString() : "null"));
        } catch (Exception e) {
            System.err.println("Failed to connect to UserQKCenter (Sync): " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testUserQKCenterAsync() {
        System.out.println("\nTesting UserQKCenter Async (http://127.0.0.1:8085)...");
        
        WebClient.Builder webClientBuilder = WebClient.builder()
                .baseUrl("http://127.0.0.1:8085")
                .defaultHeader("Accept", MediaType.TEXT_EVENT_STREAM_VALUE);
        
        ObjectMapper objectMapper = new ObjectMapper();
        WebFluxSseClientTransport transport = new WebFluxSseClientTransport(
                webClientBuilder,
                objectMapper,
                "/sse"
        );
        
        McpAsyncClient mcpAsyncClient = McpClient.async(transport)
                .clientInfo(new McpSchema.Implementation("mcp-client-demo7", "1.0.0"))
                .requestTimeout(Duration.ofSeconds(30))
                .build();
        
        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("message", "Hello from MCP Async client!");
            
            System.out.println("Sending async request to sayHello tool...");
            McpSchema.CallToolRequest request = new McpSchema.CallToolRequest("sayHello", parameters);
            Mono<McpSchema.CallToolResult> resultMono = mcpAsyncClient.callTool(request);
            McpSchema.CallToolResult result = resultMono.block();
            System.out.println("Async Response: " + (result != null ? result.toString() : "null"));
        } catch (Exception e) {
            System.err.println("Failed to connect to UserQKCenter (Async): " + e.getMessage());
            e.printStackTrace();
        }
    }
} 