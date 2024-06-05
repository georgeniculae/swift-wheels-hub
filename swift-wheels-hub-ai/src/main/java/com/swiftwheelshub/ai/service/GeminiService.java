package com.swiftwheelshub.ai.service;

import com.swiftwheelshub.dto.CarResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService {

    private final ChatDiscussionService chatDiscussionService;
    private final CarService carService;

    public String getChatOutput(HttpServletRequest request, String destination) {
        String month = LocalDate.now()
                .getMonth()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        List<CarResponse> availableCars = carService.getAllAvailableCars(request);

        return chatDiscussionService.openChatDiscussion(destination);
    }

}
