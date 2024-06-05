package com.swiftwheelshub.ai.controller;

import com.swiftwheelshub.ai.service.CarSuggestionService;
import com.swiftwheelshub.dto.TripInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/car-suggestion")
public class CarSuggestionController {

    private final CarSuggestionService carSuggestionService;

    @PostMapping
    public ResponseEntity<String> getChatPrompt(HttpServletRequest request,
                                                @RequestBody @Valid TripInfo tripInfo) {
        String chatOutput = carSuggestionService.getChatOutput(request, tripInfo);

        return ResponseEntity.ok(chatOutput);
    }

}
