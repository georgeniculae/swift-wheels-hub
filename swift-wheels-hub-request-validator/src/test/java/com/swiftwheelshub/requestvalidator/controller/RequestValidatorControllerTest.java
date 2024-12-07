package com.swiftwheelshub.requestvalidator.controller;

import com.swiftwheelshub.dto.IncomingRequestDetails;
import com.swiftwheelshub.dto.RequestValidationReport;
import com.swiftwheelshub.requestvalidator.service.RedisService;
import com.swiftwheelshub.requestvalidator.service.SwaggerRequestValidatorService;
import com.swiftwheelshub.requestvalidator.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = RequestValidatorController.class)
@AutoConfigureMockMvc
@EnableWebMvc
class RequestValidatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SwaggerRequestValidatorService swaggerRequestValidatorService;

    @MockitoBean
    private RedisService redisService;

    @Test
    void validateRequestTest_success() throws Exception {
        RequestValidationReport requestValidationReport =
                TestUtil.getResourceAsJson("/data/RequestValidationReport.json", RequestValidationReport.class);

        IncomingRequestDetails incomingRequestDetails =
                TestUtil.getResourceAsJson("/data/IncomingRequestDetails.json", IncomingRequestDetails.class);

        String content = TestUtil.writeValueAsString(incomingRequestDetails);

        when(swaggerRequestValidatorService.validateRequest(any(IncomingRequestDetails.class)))
                .thenReturn(requestValidationReport);

        mockMvc.perform(post("/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void validateRequestTest_missingRequestBody() throws Exception {
        mockMvc.perform(post("/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void invalidateSwaggerCacheTest_success() throws Exception {
        doNothing().when(redisService).repopulateRedisWithSwaggerFiles(anyString());

        MockHttpServletResponse response = mockMvc.perform(delete("/invalidate/{microserviceName}", "agency")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void invalidateSwaggerCacheTest_emptyPathVariable_notFound() throws Exception {
        mockMvc.perform(delete("/invalidate/{microserviceName}", "")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
