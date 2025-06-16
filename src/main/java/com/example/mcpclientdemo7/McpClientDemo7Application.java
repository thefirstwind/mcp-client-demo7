package com.example.mcpclientdemo7;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(excludeName = {
    "org.springframework.ai.model.openai.autoconfigure.OpenAiAudioSpeechAutoConfiguration",
    "org.springframework.ai.model.openai.autoconfigure.OpenAiAudioTranscriptionAutoConfiguration",
    "org.springframework.ai.model.openai.autoconfigure.OpenAiAutoConfiguration",
    "org.springframework.ai.model.openai.autoconfigure.OpenAiChatAutoConfiguration",
    "org.springframework.ai.model.openai.autoconfigure.OpenAiEmbeddingAutoConfiguration",
    "org.springframework.ai.model.openai.autoconfigure.OpenAiImageAutoConfiguration",
    "org.springframework.ai.model.chat.client.autoconfigure.ChatClientAutoConfiguration"
})
public class McpClientDemo7Application {
    public static void main(String[] args) {
        // Set system properties to disable OpenAI
        System.setProperty("spring.ai.openai.enabled", "false");
        System.setProperty("spring.ai.model.chat.client.enabled", "false");
        // Run the application
        SpringApplication app = new SpringApplication(McpClientDemo7Application.class);
        app.run(args);
    }
} 