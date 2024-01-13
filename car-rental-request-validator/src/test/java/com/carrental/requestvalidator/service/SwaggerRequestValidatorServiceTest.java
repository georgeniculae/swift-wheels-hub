package com.carrental.requestvalidator.service;

import com.carrental.dto.IncomingRequestDetails;
import com.carrental.dto.RequestValidationReport;
import com.carrental.requestvalidator.model.SwaggerFolder;
import com.carrental.requestvalidator.repository.SwaggerRepository;
import com.carrental.requestvalidator.util.TestUtils;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SwaggerRequestValidatorServiceTest {

    @InjectMocks
    private SwaggerRequestValidatorService swaggerRequestValidatorService;

    @Mock
    private SwaggerRepository swaggerRepository;

    @Test
    void validateRequestTest_success() {
        IncomingRequestDetails incomingRequestDetails =
                TestUtils.getResourceAsJson("/data/IncomingRequestDetails.json", IncomingRequestDetails.class);

        String content = new OpenAPIV3Parser()
                .read("src/test/resources/swagger-definition/car-rental-agency.yaml")
                .toString();

        SwaggerFolder swaggerFolder = SwaggerFolder.builder()
                .id("agency")
                .swaggerContent(content)
                .build();

        when(swaggerRepository.findById(anyString())).thenReturn(Optional.ofNullable(swaggerFolder));

        RequestValidationReport requestValidationReport =
                swaggerRequestValidatorService.validateRequest(incomingRequestDetails);

        assertTrue(requestValidationReport.errorMessage().isEmpty());
    }

}
