package com.swiftwheelshub.ai.controller;

import com.swiftwheelshub.ai.service.GeminiService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/car-suggestion")
public class CarSuggestionController {

    private final GeminiService geminiService;

    @PostMapping
    public ResponseEntity<String> getChatPrompt(HttpServletRequest request,
                                                @RequestBody @NonNull String destination) {
        String chatOutput = geminiService.getChatOutput(request, destination);

        return ResponseEntity.ok(chatOutput);
    }

}
