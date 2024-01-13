package com.carrental.requestvalidator.controller;

import com.carrental.requestvalidator.service.RedisService;
import com.carrental.requestvalidator.service.SwaggerRequestValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootTest(classes = RequestValidatorController.class)
@AutoConfigureMockMvc
@EnableWebMvc
class RequestValidatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SwaggerRequestValidatorService swaggerRequestValidatorService;

    @MockBean
    private RedisService redisService;

}
