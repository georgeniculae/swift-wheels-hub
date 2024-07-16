package com.swiftwheelshub.ai.controller;

import com.swiftwheelshub.ai.service.CarSuggestionService;
import com.swiftwheelshub.dto.CarSuggestionResponse;
import com.swiftwheelshub.dto.TripInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<CarSuggestionResponse> getChatPrompt(@Validated TripInfo tripInfo) {
        CarSuggestionResponse carSuggestionResponse = carSuggestionService.getChatOutput(tripInfo);

        return ResponseEntity.ok(carSuggestionResponse);
    }

}
