package com.swiftwheelshub.requestvalidator.service;

import com.swiftwheelshub.exception.SwiftWheelsHubException;
import com.swiftwheelshub.requestvalidator.util.TestUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @BeforeEach
    public void setupUrls() {
        ReflectionTestUtils.setField(swaggerExtractorService, "agencyApiDocUrl", "/agency");
        ReflectionTestUtils.setField(swaggerExtractorService, "bookingApiDocUrl", "/booking");
        ReflectionTestUtils.setField(swaggerExtractorService, "customerApiDocUrl", "/customer");
        ReflectionTestUtils.setField(swaggerExtractorService, "expenseApiDocUrl", "/expense");
    }

    @Test
    @SuppressWarnings("unchecked")
    void getSwaggerIdentifierAndContent_success() {
        String content =
                TestUtils.getResourceAsJson("/data/SwiftWheelsHubAgencySwagger.json", String.class);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), any(String[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(eq(String.class))).thenReturn(content);

        Map<String, String> swaggerIdentifierAndContent = swaggerExtractorService.getSwaggerIdentifierAndContent();
        assertFalse(swaggerIdentifierAndContent.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getSwaggerIdentifierAndContent_emptySwagger() {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), any(String[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(eq(String.class))).thenReturn(StringUtils.EMPTY);

        assertThrows(SwiftWheelsHubException.class, () -> swaggerExtractorService.getSwaggerIdentifierAndContent());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getSwaggerFileForMicroservice_agency_success() {
        String content =
                TestUtils.getResourceAsJson("/data/SwiftWheelsHubAgencySwagger.json", String.class);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), any(String[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(eq(String.class))).thenReturn(content);

        Map<String, String> agency = swaggerExtractorService.getSwaggerFileForMicroservice("agency");
        assertFalse(agency.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getSwaggerFileForMicroservice_bookings_success() {
        String content =
                TestUtils.getResourceAsJson("/data/SwiftWheelsHubBookingsSwagger.json", String.class);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), any(String[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(eq(String.class))).thenReturn(content);

        Map<String, String> agency = swaggerExtractorService.getSwaggerFileForMicroservice("bookings");
        assertFalse(agency.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getSwaggerFileForMicroservice_customers_success() {
        String content =
                TestUtils.getResourceAsJson("/data/SwiftWheelsHubCustomersSwagger.json", String.class);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), any(String[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(eq(String.class))).thenReturn(content);

        Map<String, String> agency = swaggerExtractorService.getSwaggerFileForMicroservice("customers");
        assertFalse(agency.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getSwaggerFileForMicroservice_expense_success() {
        String content =
                TestUtils.getResourceAsJson("/data/SwiftWheelsHubExpenseSwagger.json", String.class);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), any(String[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(eq(String.class))).thenReturn(content);

        Map<String, String> agency = swaggerExtractorService.getSwaggerFileForMicroservice("expense");
        assertFalse(agency.isEmpty());
    }

    @Test
    void getSwaggerFileForMicroservice_nonexistentMicroservice() {
        assertThrows(SwiftWheelsHubException.class, () -> swaggerExtractorService.getSwaggerFileForMicroservice("test"));
    }

}
