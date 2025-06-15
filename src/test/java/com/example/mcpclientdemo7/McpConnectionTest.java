//package com.example.mcpclientdemo7;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.modelcontextprotocol.client.McpAsyncClient;
//import io.modelcontextprotocol.client.McpClient;
//import io.modelcontextprotocol.client.transport.WebFluxSseClientTransport;
//import io.modelcontextprotocol.spec.McpSchema;
//import org.junit.jupiter.api.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.web.reactive.function.client.ExchangeStrategies;
//import org.springframework.web.reactive.function.client.WebClient;
//
//import java.time.Duration;
//
//public class McpConnectionTest {
//
//    private static final Logger log = LoggerFactory.getLogger(McpConnectionTest.class);
//
//    @Test
//    public void testMcpConnection() {
//        // This test is just to verify the connection setup, not to actually run
//        log.info("Setting up MCP connection test");
//
//        // Set up WebClient
//        final int size = 16 * 1024 * 1024; // 16MB
//        final ExchangeStrategies strategies = ExchangeStrategies.builder()
//                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
//                .build();
//
//        WebClient.Builder webClientBuilder = WebClient.builder()
//                .baseUrl("http://127.0.0.1:8084")
//                .exchangeStrategies(strategies)
//                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                .defaultHeader(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        // Use builder pattern from official SDK implementation
//        WebFluxSseClientTransport transport = WebFluxSseClientTransport.builder(webClientBuilder)
//                .objectMapper(objectMapper)
//                .sseEndpoint("/sse")
//                .build();
//
//        // Create sync client
//        McpClient.SyncSpec syncSpec = McpClient.sync(transport);
//        syncSpec.clientInfo(new McpSchema.Implementation("mcp-client-demo7-test", "1.0.0"));
//        syncSpec.requestTimeout(Duration.ofSeconds(60));
//
//        log.info("MCP connection test setup completed successfully");
//    }
//}