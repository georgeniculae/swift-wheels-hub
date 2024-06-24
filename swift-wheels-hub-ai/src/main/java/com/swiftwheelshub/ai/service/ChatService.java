package com.swiftwheelshub.ai.service;

import com.swiftwheelshub.dto.CarSuggestionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient chatClient;

    public CarSuggestionResponse getChatReply(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .call()
                .entity(CarSuggestionResponse.class);
    }

}
