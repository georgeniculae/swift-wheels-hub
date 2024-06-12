package com.swiftwheelshub.ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient chatClient;

    public String getChatReply(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

}
