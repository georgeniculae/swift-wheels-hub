package com.swiftwheelshub.ai.config;

import io.micrometer.observation.ObservationRegistry;
import jakarta.persistence.criteria.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.model.function.FunctionCallbackContext;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ChatLanguageModelConfig {


    @Bean
    public OllamaApi ollamaApi(@Value("${spring.ai.ollama.base-url}") String baseUrl) {
        return new OllamaApi(baseUrl);
    }

//    @Bean
//    public ChatModel chatModel(OllamaApi ollamaApi, ChatProperties chatProperties) {
//        return new OllamaChatModel(
//                ollamaApi,
//                OllamaOptions.builder()
//                        .withModel(chatProperties.getModel())
//                        .withTemperature(chatProperties.getTemperature())
//                        .build(),
//                new FunctionCallbackContext(),
//                List.of(
//                        FunctionCallback.builder()
//                                .function("suggestCar", _ -> "")
//                                .inputType(Order.class)
//                                .build()
//                ),
//                ObservationRegistry.create(),
//                ModelManagementOptions.builder().withTimeout(Duration.ofSeconds(30)).build()
//        );
//    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder.defaultSystem("""
                        You are a helpful assistant who can clearly and concisely answer questions about the type
                        of vehicle that is most suitable for traveling to a certain location in Romania in a certain
                        month of the year. You must provide one suggestion only.""")
                .build();
    }

}
