package com.autohub.ai.controller;

import com.autohub.ai.service.CarSuggestionService;
import com.autohub.dto.ai.CarSuggestionResponse;
import com.autohub.dto.ai.TripInfo;
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
