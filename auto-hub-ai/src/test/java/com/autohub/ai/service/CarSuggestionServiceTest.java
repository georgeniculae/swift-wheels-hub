package com.autohub.ai.service;

import com.autohub.ai.util.TestUtil;
import com.autohub.dto.common.AuthenticationInfo;
import com.autohub.dto.agency.CarResponse;
import com.autohub.dto.ai.CarSuggestionResponse;
import com.autohub.dto.ai.TripInfo;
import com.autohub.lib.security.ApiKeyAuthenticationToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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
                TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);
        TripInfo tripInfo =
                TestUtil.getResourceAsJson("/data/TripInfo.json", TripInfo.class);
        CarSuggestionResponse carSuggestionResponse =
                TestUtil.getResourceAsJson("/data/CarSuggestionResponse.json", CarSuggestionResponse.class);

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.addHeader("X-API-KEY", "apikey");
        httpServletRequest.addHeader("X-ROLES", "ROLE_user");

        RequestAttributes servletWebRequest = new ServletWebRequest(httpServletRequest);
        RequestContextHolder.setRequestAttributes(servletWebRequest);

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");
        ApiKeyAuthenticationToken apiKeyAuthenticationToken =
                new ApiKeyAuthenticationToken(List.of(simpleGrantedAuthority), "apikey");

        SecurityContextHolder.getContext().setAuthentication(apiKeyAuthenticationToken);

        when(carService.getAllAvailableCars(any(AuthenticationInfo.class))).thenReturn(List.of(carResponse));
        when(chatService.getChatReply(anyString(), anyMap())).thenReturn(carSuggestionResponse);

        CarSuggestionResponse actualCarSuggestionResponse = carSuggestionService.getChatOutput(tripInfo);
        assertNotNull(actualCarSuggestionResponse);
    }

}
