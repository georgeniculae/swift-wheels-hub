package com.swiftwheelshub.ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeminiService {

    private final ChatClient chatClient;

    public String openChatDiscussion(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .call()
                .entity(String.class);
    }

}
