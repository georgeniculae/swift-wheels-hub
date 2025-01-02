package com.swiftwheelshub.ai.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ChatLanguageModelConfig {

    @Bean
    public OllamaApi ollamaApi() {
        return new OllamaApi();
    }

    @Bean
    public ChatModel chatModel(OllamaApi ollamaApi, ChatProperties chatProperties) {
        return OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(
                        OllamaOptions.builder()
                                .model(chatProperties.getModel())
                                .temperature(chatProperties.getTemperature())
                                .build()
                )
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder.defaultSystem("""
                        You are a helpful assistant who can clearly and concisely answer questions about the type
                        of vehicle that is most suitable for traveling to a certain location in Romania in a certain
                        month of the year. You must provide one suggestion only.""")
                .build();
    }

}
