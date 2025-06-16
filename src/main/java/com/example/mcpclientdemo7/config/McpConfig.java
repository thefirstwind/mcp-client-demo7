package com.example.mcpclientdemo7.config;

import com.example.mcpclientdemo7.model.McpToolResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.WebFluxSseClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.resolver.DefaultAddressResolverGroup;
import io.netty.util.Timeout;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Schedulers;
import reactor.netty.resources.ConnectionProvider;

import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import java.time.Duration;
import java.util.List;
import java.time.Duration;
import java.util.concurrent.TimeUnit;


@Configuration
public class McpConfig {
    private static final Logger log = LoggerFactory.getLogger(McpConfig.class);

    @Value("${mcp.server.usercenter.url:http://127.0.0.1:8084}")
    private String userCenterUrl;

    @Value("${mcp.server.userqkcenter.url:http://127.0.0.1:8085}")
    private String userQKCenterUrl;
    
    @Value("${mcp.server.usercenter.sse-path:/sse}")
    private String userCenterSsePath;

    @Value("${mcp.server.userqkcenter.sse-path:/sse}")
    private String userQKCenterSsePath;

    private Timeout timeout = new Timeout();
    @Data
    public static class Timeout {
        private Duration connect;
        private Duration response;
        private Duration read;
        private Duration write;
    }
    /**
     * Pool Configuration
     */
    private Pool pool = new Pool();

    @Data
    public static class Pool {
        private int maxConnections;
        private Duration maxIdleTime;
        private Duration maxLifeTime;
        private Duration pendingAcquireTimeout;
        private Duration evictInBackground;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public WebClient userCenterWebClient() {
        // Increase buffer size to handle larger SSE messages
        final int size = 16 * 1024 * 1024; // 16MB
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();

        return WebClient.builder()
                .baseUrl(userCenterUrl)
                .exchangeStrategies(strategies)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)
                .build();
    }

    @Bean
    public WebClient userQKCenterWebClient() {
        // Increase buffer size to handle larger SSE messages
        final int size = 16 * 1024 * 1024; // 16MB
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();

        return WebClient.builder()
                .baseUrl(userQKCenterUrl)
                .exchangeStrategies(strategies)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)
                .build();
    }

    @Bean
    public WebFluxSseClientTransport userCenterTransport(ObjectMapper objectMapper) {
        // Increase buffer size to handle larger SSE messages
        final int size = 16 * 1024 * 1024; // 16MB
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();

        HttpClient httpClient = HttpClient.create().resolver(DefaultAddressResolverGroup.INSTANCE);

        WebClient.Builder webClientBuilder = WebClient.builder()
                .baseUrl(userCenterUrl)
                .exchangeStrategies(strategies)
//                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE);
        
        // Use builder pattern from official SDK implementation
        return WebFluxSseClientTransport.builder(webClientBuilder)
                .objectMapper(objectMapper)
                .sseEndpoint(userCenterSsePath)
                .build();
    }
    
    @Bean
    public WebFluxSseClientTransport userQKCenterTransport(ObjectMapper objectMapper) {
        // Increase buffer size to handle larger SSE messages
        final int size = 16 * 1024 * 1024; // 16MB
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();
        HttpClient httpClient = HttpClient.create().resolver(DefaultAddressResolverGroup.INSTANCE);
        WebClient.Builder webClientBuilder = WebClient.builder()
                .baseUrl(userQKCenterUrl)
                .exchangeStrategies(strategies)
//                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE);
        
        // Use builder pattern from official SDK implementation
        return WebFluxSseClientTransport.builder(webClientBuilder)
                .objectMapper(objectMapper)
                .sseEndpoint(userQKCenterSsePath)
                .build();
    }
    
    @Bean
    public McpSyncClient userCenterMcpClient(WebFluxSseClientTransport userCenterTransport) {
        McpClient.SyncSpec syncSpec = McpClient.sync(userCenterTransport);
        syncSpec.clientInfo(new McpSchema.Implementation("mcp-client-demo7", "1.0.0"));
        syncSpec.requestTimeout(Duration.ofSeconds(60));
        return syncSpec.build();
    }
    
    @Bean
    public McpSyncClient userQKCenterMcpClient(WebFluxSseClientTransport userQKCenterTransport) {
        McpClient.SyncSpec syncSpec = McpClient.sync(userQKCenterTransport);
        syncSpec.clientInfo(new McpSchema.Implementation("mcp-client-demo7", "1.0.0"));
        syncSpec.requestTimeout(Duration.ofSeconds(60));
        return syncSpec.build();
    }
    
    @Bean
    public McpAsyncClient userCenterMcpAsyncClient(WebFluxSseClientTransport userCenterTransport) {
        McpClient.AsyncSpec asyncSpec = McpClient.async(userCenterTransport);
        asyncSpec.clientInfo(new McpSchema.Implementation("mcp-client-demo7", "1.0.0"));
        asyncSpec.requestTimeout(Duration.ofSeconds(60));
        return asyncSpec.build();
    }
    
    @Bean
    public McpAsyncClient userQKCenterMcpAsyncClient(WebFluxSseClientTransport userQKCenterTransport) {
        McpClient.AsyncSpec asyncSpec = McpClient.async(userQKCenterTransport);
        asyncSpec.clientInfo(new McpSchema.Implementation("mcp-client-demo7", "1.0.0"));
        asyncSpec.requestTimeout(Duration.ofSeconds(60));
        return asyncSpec.build();
    }
} 