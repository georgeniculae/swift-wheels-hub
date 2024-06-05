package com.swiftwheelshub.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService {

    private final ChatDiscussionService chatDiscussionService;
    private final CarService carService;

    public String getChatOutput(String destination) {
        String month = LocalDate.now()
                .getMonth()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        return chatDiscussionService.openChatDiscussion(destination);
    }

}
