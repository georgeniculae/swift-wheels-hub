package com.swiftwheelshub.requestvalidator.service;

import com.swiftwheelshub.exception.SwiftWheelsHubException;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.requestvalidator.config.RegisteredEndpoints;
import com.swiftwheelshub.requestvalidator.model.SwaggerFile;
import com.swiftwheelshub.requestvalidator.util.TestUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SwaggerExtractorServiceTest {

    @InjectMocks
    private SwaggerExtractorService swaggerExtractorService;

    @Mock
    private RestClient restClient;

    @Mock
    @SuppressWarnings("rawtypes")
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    @SuppressWarnings("rawtypes")
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @Mock
    private RegisteredEndpoints registeredEndpoints;

    @Test
    @SuppressWarnings("all")
    void getSwaggerFiles_success() {
        String agencyContent =
                TestUtil.getResourceAsJson("/data/SwiftWheelsHubAgencySwagger.json", String.class);
        String bookingContent =
                TestUtil.getResourceAsJson("/data/SwiftWheelsHubBookingSwagger.json", String.class);
        String customerContent =
                TestUtil.getResourceAsJson("/data/SwiftWheelsHubCustomerSwagger.json", String.class);
        String expenseContent =
                TestUtil.getResourceAsJson("/data/SwiftWheelsHubExpenseSwagger.json", String.class);

        Map<String, String> endpoints = new LinkedHashMap<>();
        endpoints.put("agency", agencyContent);
        endpoints.put("booking", bookingContent);
        endpoints.put("customer", customerContent);
        endpoints.put("expense", expenseContent);

        when(registeredEndpoints.getEndpoints()).thenReturn(endpoints);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), any(String[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(Predicate.class), any(RestClient.ResponseSpec.ErrorHandler.class)))
                .thenReturn(responseSpec);

        when(responseSpec.body(eq(String.class))).thenAnswer(new Answer() {
            private int count = 0;

            public Object answer(InvocationOnMock invocation) {
                count++;

                if (count == 1) {
                    return agencyContent;
                } else if (count == 2) {
                    return bookingContent;
                } else if (count == 3) {
                    return customerContent;
                } else {
                    return expenseContent;
                }
            }
        });

        List<SwaggerFile> swaggers = swaggerExtractorService.getSwaggerFiles();
        assertFalse(swaggers.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getSwaggerFiles_emptySwagger() {
        String agencyContent =
                TestUtil.getResourceAsJson("/data/SwiftWheelsHubAgencySwagger.json", String.class);
        String bookingContent =
                TestUtil.getResourceAsJson("/data/SwiftWheelsHubBookingSwagger.json", String.class);
        String customerContent =
                TestUtil.getResourceAsJson("/data/SwiftWheelsHubCustomerSwagger.json", String.class);
        String expenseContent =
                TestUtil.getResourceAsJson("/data/SwiftWheelsHubExpenseSwagger.json", String.class);

        Map<String, String> endpoints = new LinkedHashMap<>();
        endpoints.put("agency", agencyContent);
        endpoints.put("booking", bookingContent);
        endpoints.put("customer", customerContent);
        endpoints.put("expense", expenseContent);

        when(registeredEndpoints.getEndpoints()).thenReturn(endpoints);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), any(String[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(eq(String.class))).thenReturn(StringUtils.EMPTY);
        when(responseSpec.onStatus(any(Predicate.class), any(RestClient.ResponseSpec.ErrorHandler.class)))
                .thenReturn(responseSpec);

        assertThrows(SwiftWheelsHubException.class, () -> swaggerExtractorService.getSwaggerFiles());
    }

    @Test
    @SuppressWarnings("all")
    void getSwaggerFileForMicroservice_success() {
        String agencyContent =
                TestUtil.getResourceAsJson("/data/SwiftWheelsHubAgencySwagger.json", String.class);
        String bookingContent =
                TestUtil.getResourceAsJson("/data/SwiftWheelsHubBookingSwagger.json", String.class);
        String customerContent =
                TestUtil.getResourceAsJson("/data/SwiftWheelsHubCustomerSwagger.json", String.class);
        String expenseContent =
                TestUtil.getResourceAsJson("/data/SwiftWheelsHubExpenseSwagger.json", String.class);

        Map<String, String> endpoints = new LinkedHashMap<>();
        endpoints.put("agency", agencyContent);
        endpoints.put("booking", bookingContent);
        endpoints.put("customer", customerContent);
        endpoints.put("expense", expenseContent);

        when(registeredEndpoints.getEndpoints()).thenReturn(endpoints);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), any(String[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(Predicate.class), any(RestClient.ResponseSpec.ErrorHandler.class)))
                .thenReturn(responseSpec);

        when(responseSpec.body(eq(String.class))).thenAnswer(new Answer() {
            private int count = 0;

            public Object answer(InvocationOnMock invocation) {
                count++;

                if (count == 1) {
                    return agencyContent;
                } else if (count == 2) {
                    return bookingContent;
                } else if (count == 3) {
                    return customerContent;
                } else {
                    return expenseContent;
                }
            }
        });

        SwaggerFile swagger = swaggerExtractorService.getSwaggerFileForMicroservice("bookings");
        assertTrue(ObjectUtils.isNotEmpty(swagger));
    }

    @Test
    @SuppressWarnings("all")
    void getSwaggerFileForMicroservice_nonexistentMicroservice() {
        String agencyContent =
                TestUtil.getResourceAsJson("/data/SwiftWheelsHubAgencySwagger.json", String.class);
        String bookingContent =
                TestUtil.getResourceAsJson("/data/SwiftWheelsHubBookingSwagger.json", String.class);
        String customerContent =
                TestUtil.getResourceAsJson("/data/SwiftWheelsHubCustomerSwagger.json", String.class);
        String expenseContent =
                TestUtil.getResourceAsJson("/data/SwiftWheelsHubExpenseSwagger.json", String.class);

        Map<String, String> endpoints = new LinkedHashMap<>();
        endpoints.put("agency", agencyContent);
        endpoints.put("booking", bookingContent);
        endpoints.put("customer", customerContent);
        endpoints.put("expense", expenseContent);

        when(registeredEndpoints.getEndpoints()).thenReturn(endpoints);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), any(String[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(Predicate.class), any(RestClient.ResponseSpec.ErrorHandler.class)))
                .thenReturn(responseSpec);

        when(responseSpec.body(eq(String.class))).thenAnswer(new Answer() {
            private int count = 0;

            public Object answer(InvocationOnMock invocation) {
                count++;

                if (count == 1) {
                    return agencyContent;
                } else if (count == 2) {
                    return bookingContent;
                } else if (count == 3) {
                    return customerContent;
                } else {
                    return expenseContent;
                }
            }
        });

        assertThrows(SwiftWheelsHubNotFoundException.class, () -> swaggerExtractorService.getSwaggerFileForMicroservice("test"));
    }

}
