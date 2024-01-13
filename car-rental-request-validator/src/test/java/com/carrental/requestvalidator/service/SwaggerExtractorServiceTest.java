package com.carrental.requestvalidator.service;

import com.carrental.exception.CarRentalException;
import io.swagger.v3.parser.OpenAPIV3Parser;
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
import static org.mockito.ArgumentMatchers.*;
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
        ReflectionTestUtils.setField(swaggerExtractorService, "agencyApiDocUrl", "agency");
        ReflectionTestUtils.setField(swaggerExtractorService, "bookingApiDocUrl", "booking");
        ReflectionTestUtils.setField(swaggerExtractorService, "customerApiDocUrl", "customer");
        ReflectionTestUtils.setField(swaggerExtractorService, "expenseApiDocUrl", "expense");
    }

    @Test
    @SuppressWarnings("unchecked")
    void getSwaggerIdentifierAndContent_success() {
        String content = new OpenAPIV3Parser()
                .read("src/test/resources/swagger-definition/car-rental-agency.yaml")
                .toString();

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

        assertThrows(CarRentalException.class, () -> swaggerExtractorService.getSwaggerIdentifierAndContent());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getSwaggerFileForMicroservice_agency_success() {
        String content = new OpenAPIV3Parser()
                .read("src/test/resources/swagger-definition/car-rental-agency.yaml")
                .toString();

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
        String content = new OpenAPIV3Parser()
                .read("src/test/resources/swagger-definition/car-rental-bookings.yaml")
                .toString();

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
        String content = new OpenAPIV3Parser()
                .read("src/test/resources/swagger-definition/car-rental-bookings.yaml")
                .toString();

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
        String content = new OpenAPIV3Parser()
                .read("src/test/resources/swagger-definition/car-rental-expense.yaml")
                .toString();

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
        assertThrows(CarRentalException.class, () -> swaggerExtractorService.getSwaggerFileForMicroservice("test"));
    }

}
