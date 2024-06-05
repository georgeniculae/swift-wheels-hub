package com.swiftwheelshub.ai.controller;

import com.swiftwheelshub.ai.service.CarSuggestionService;
import com.swiftwheelshub.ai.util.TestUtils;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
        TripInfo tripInfo = TestUtils.getResourceAsJson("/data/TripInfo.json", TripInfo.class);
        String content = TestUtils.writeValueAsString(tripInfo);

        when(carSuggestionService.getChatOutput(any(HttpServletRequest.class), any(TripInfo.class))).thenReturn("Test");

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post(PATH + "/car-suggestion")
                        .contextPath(PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void getChatPromptTest_unauthorized() throws Exception {
        TripInfo tripInfo = TestUtils.getResourceAsJson("/data/TripInfo.json", TripInfo.class);
        String content = TestUtils.writeValueAsString(tripInfo);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post(PATH + "/car-suggestion")
                        .contextPath(PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void getChatPromptTest_noRequestBody() throws Exception {
        TripInfo tripInfo = TestUtils.getResourceAsJson("/data/TripInfo.json", TripInfo.class);
        TripInfo invalidTripInfo = tripInfo.toBuilder().destination("").build();
        String content = TestUtils.writeValueAsString(invalidTripInfo);

        when(carSuggestionService.getChatOutput(any(HttpServletRequest.class), any(TripInfo.class))).thenReturn("Test");

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post(PATH + "/car-suggestion")
                        .contextPath(PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

}
