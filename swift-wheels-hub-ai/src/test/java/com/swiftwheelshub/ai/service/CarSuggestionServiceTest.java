package com.swiftwheelshub.ai.service;

import com.swiftwheelshub.ai.util.TestUtils;
import com.swiftwheelshub.dto.CarResponse;
import com.swiftwheelshub.dto.CarSuggestionResponse;
import com.swiftwheelshub.dto.TripInfo;
import com.swiftwheelshub.lib.security.ApiKeyAuthenticationToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarSuggestionServiceTest {

    @InjectMocks
    private CarSuggestionService carSuggestionService;

    @Mock
    private CarService carService;

    @Mock
    private ChatService chatService;

    @Test
    void getChatOutputTest_success() {
        CarResponse carResponse =
                TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);
        TripInfo tripInfo =
                TestUtils.getResourceAsJson("/data/TripInfo.json", TripInfo.class);
        CarSuggestionResponse carSuggestionResponse =
                TestUtils.getResourceAsJson("/data/CarSuggestionResponse.json", CarSuggestionResponse.class);

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");
        ApiKeyAuthenticationToken apiKeyAuthenticationToken =
                new ApiKeyAuthenticationToken(List.of(simpleGrantedAuthority), "apikey");

        SecurityContextHolder.getContext().setAuthentication(apiKeyAuthenticationToken);

        when(carService.getAllAvailableCars(anyString(), anyCollection())).thenReturn(List.of(carResponse));
        when(chatService.getChatReply(anyString(), anyMap())).thenReturn(carSuggestionResponse);

        CarSuggestionResponse actualCarSuggestionResponse = carSuggestionService.getChatOutput(tripInfo);
        assertNotNull(actualCarSuggestionResponse);
    }

}
