package com.carrental.requestvalidator.service;

import com.carrental.requestvalidator.repository.SwaggerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SwaggerRequestValidatorServiceTest {

    @InjectMocks
    private SwaggerRequestValidatorService swaggerRequestValidatorService;

    @Mock
    private SwaggerRepository swaggerRepository;

    @Test
    void validateRequestTest_success() {

    }

}
