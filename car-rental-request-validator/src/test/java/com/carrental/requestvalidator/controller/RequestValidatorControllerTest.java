package com.carrental.requestvalidator.controller;

import com.carrental.dto.IncomingRequestDetails;
import com.carrental.dto.RequestValidationReport;
import com.carrental.requestvalidator.service.RedisService;
import com.carrental.requestvalidator.service.SwaggerRequestValidatorService;
import com.carrental.requestvalidator.util.TestUtils;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = RequestValidatorController.class)
@AutoConfigureMockMvc
@EnableWebMvc
class RequestValidatorControllerTest {

    private static final String PATH = "/request-validator";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SwaggerRequestValidatorService swaggerRequestValidatorService;

    @MockBean
    private RedisService redisService;

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void validateRequestTest_success() throws Exception {
        RequestValidationReport requestValidationReport =
                TestUtils.getResourceAsJson("/data/RequestValidationReport.json", RequestValidationReport.class);

        IncomingRequestDetails incomingRequestDetails =
                TestUtils.getResourceAsJson("/data/IncomingRequestDetails.json", IncomingRequestDetails.class);

        String content = TestUtils.writeValueAsString(incomingRequestDetails);

        when(swaggerRequestValidatorService.validateRequest(any(IncomingRequestDetails.class)))
                .thenReturn(requestValidationReport);

        mockMvc.perform(MockMvcRequestBuilders.post(PATH + "/validate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @WithAnonymousUser
    void validateRequestTest_unauthorized() throws Exception {
        RequestValidationReport requestValidationReport =
                TestUtils.getResourceAsJson("/data/RequestValidationReport.json", RequestValidationReport.class);

        IncomingRequestDetails incomingRequestDetails =
                TestUtils.getResourceAsJson("/data/IncomingRequestDetails.json", IncomingRequestDetails.class);

        String content = TestUtils.writeValueAsString(incomingRequestDetails);

        when(swaggerRequestValidatorService.validateRequest(any(IncomingRequestDetails.class)))
                .thenReturn(requestValidationReport);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post(PATH + "/validate")
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
    void invalidateSwaggerCacheTest_success() throws Exception {
        doNothing().when(redisService).repopulateRedisWithSwaggerFolder(anyString());

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.put(PATH + "/invalidate/{microserviceName}", "agency")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void invalidateSwaggerCacheTest_unauthorized() throws Exception {
        doNothing().when(redisService).repopulateRedisWithSwaggerFolder(anyString());

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.put(PATH + "/invalidate/{microserviceName}", "agency")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

}
