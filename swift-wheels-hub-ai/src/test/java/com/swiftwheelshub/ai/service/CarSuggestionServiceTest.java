package com.swiftwheelshub.ai.service;

import com.swiftwheelshub.ai.util.TestUtils;
import com.swiftwheelshub.dto.CarResponse;
import com.swiftwheelshub.dto.TripInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarSuggestionServiceTest {

    @InjectMocks
    private CarSuggestionService carSuggestionService;

    @Mock
    private CarService carService;

    @Mock
    private GeminiService geminiService;

    @Test
    void getChatOutputTest_success() {
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        String output = "Test";
        TripInfo tripInfo = TestUtils.getResourceAsJson("/data/TripInfo.json", TripInfo.class);

        when(carService.getAllAvailableCars(any(HttpServletRequest.class))).thenReturn(List.of(carResponse));
        when(geminiService.openChatDiscussion(anyString())).thenReturn(output);

        String chatOutput = carSuggestionService.getChatOutput(request, tripInfo);
        assertNotNull(chatOutput);
    }

}
