package com.swiftwheelshub.ai.controller;

import com.swiftwheelshub.ai.service.CarSuggestionService;
import com.swiftwheelshub.ai.util.TestUtils;
import com.swiftwheelshub.dto.CarSuggestionResponse;
import com.swiftwheelshub.dto.TripInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = CarSuggestionController.class)
@AutoConfigureMockMvc
@EnableWebMvc
class CarSuggestionControllerTest {

    private static final String PATH = "/ai";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarSuggestionService carSuggestionService;

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void getChatPromptTest_success() throws Exception {
        CarSuggestionResponse carSuggestionResponse =
                TestUtils.getResourceAsJson("/data/CarSuggestionResponse.json", CarSuggestionResponse.class);

        when(carSuggestionService.getChatOutput(any(HttpServletRequest.class), any(TripInfo.class)))
                .thenReturn(carSuggestionResponse);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/car-suggestion?destination=Sinaia&peopleCount=3&tripKind=city&tripDate=2024-06-20")
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void getChatPromptTest_unauthorized() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/car-suggestion?destination=Sinaia&peopleCount=3&tripKind=city&tripDate=2024-06-20")
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void getChatPromptTest_missingRequestParam() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/car-suggestion?destination=Sinaia&peopleCount=3&tripKind=city")
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

}
