package com.swiftwheelshub.ai.controller;

import com.swiftwheelshub.ai.service.CarSuggestionService;
import com.swiftwheelshub.dto.CarSuggestionResponse;
import com.swiftwheelshub.dto.TripInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/car-suggestion")
public class CarSuggestionController {

    private final CarSuggestionService carSuggestionService;

    @GetMapping
    public ResponseEntity<CarSuggestionResponse> getChatPrompt(HttpServletRequest request, @Validated TripInfo tripInfo) {
        CarSuggestionResponse carSuggestionResponse = carSuggestionService.getChatOutput(request, tripInfo);

        return ResponseEntity.ok(carSuggestionResponse);
    }

}
