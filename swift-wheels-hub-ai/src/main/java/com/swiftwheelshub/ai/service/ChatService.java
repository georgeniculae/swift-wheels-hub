package com.swiftwheelshub.ai.service;

import com.swiftwheelshub.dto.CarSuggestionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient chatClient;

    public CarSuggestionResponse getChatReply(String text, Map<String, Object> params) {
        return chatClient.prompt()
                .user(userSpec -> userSpec.text(text).params(params))
                .call()
                .entity(CarSuggestionResponse.class);
    }

}
