package com.autohub.requestvalidator.service;

import com.autohub.dto.IncomingRequestDetails;
import com.autohub.dto.RequestValidationReport;
import com.autohub.requestvalidator.model.SwaggerFile;
import com.autohub.requestvalidator.repository.SwaggerRepository;
import com.autohub.requestvalidator.util.TestUtil;
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
                TestUtil.getResourceAsJson("/data/IncomingRequestDetails.json", IncomingRequestDetails.class);

        String content =
                TestUtil.getResourceAsJson("/data/AutoHubAgencySwagger.json", String.class);

        SwaggerFile swaggerFile = SwaggerFile.builder()
                .identifier("agency")
                .swaggerContent(content)
                .build();

        when(swaggerRepository.findById(anyString())).thenReturn(Optional.ofNullable(swaggerFile));

        RequestValidationReport requestValidationReport =
                swaggerRequestValidatorService.validateRequest(incomingRequestDetails);

        assertTrue(requestValidationReport.errorMessage().isEmpty());
    }

}
