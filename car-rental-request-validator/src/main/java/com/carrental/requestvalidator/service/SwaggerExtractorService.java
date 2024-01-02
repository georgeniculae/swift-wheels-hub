package com.carrental.requestvalidator.service;

import com.carrental.exception.CarRentalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class SwaggerExtractorService {

    private static final String X_API_KEY = "X-API-KEY";

    private static final String AGENCY = "agency";

    private static final String BOOKINGS = "bookings";

    private static final String CUSTOMERS = "customers";

    private static final String EXPENSE = "expense";

    @Value("${swagger.agency}")
    private String agencyApiDocUrl;

    @Value("${swagger.booking}")
    private String bookingApiDocUrl;

    @Value("${swagger.customer}")
    private String customerApiDocUrl;

    @Value("${swagger.expense}")
    private String expenseApiDocUrl;

    @Value("${apikey-secret}")
    private String apikey;

    private final RestClient restClient;

    public Map<String, String> getSwaggerIdentifierAndContent() {
        CompletableFuture<Map<String, String>> agencySwagger =
                CompletableFuture.supplyAsync(this::getCarRentalAgencySwagger);

        CompletableFuture<Map<String, String>> bookingSwagger =
                CompletableFuture.supplyAsync(this::getCarRentalBookingSwagger);

        CompletableFuture<Map<String, String>> customerSwagger =
                CompletableFuture.supplyAsync(this::getCarRentalCustomerSwagger);

        CompletableFuture<Map<String, String>> expenseSwagger =
                CompletableFuture.supplyAsync(this::getCarRentalExpenseSwagger);

        return getSwaggersAndIdentifiers(agencySwagger, bookingSwagger, customerSwagger, expenseSwagger);
    }

    private Map<String, String> getSwaggersAndIdentifiers(CompletableFuture<Map<String, String>> agencySwagger,
                                                          CompletableFuture<Map<String, String>> bookingSwagger,
                                                          CompletableFuture<Map<String, String>> customerSwagger,
                                                          CompletableFuture<Map<String, String>> expenseSwagger) {

        Map<String, String> swaggersAndIdentifiers = new HashMap<>();

        swaggersAndIdentifiers.putAll(agencySwagger.join());
        swaggersAndIdentifiers.putAll(bookingSwagger.join());
        swaggersAndIdentifiers.putAll(customerSwagger.join());
        swaggersAndIdentifiers.putAll(expenseSwagger.join());

        return Collections.unmodifiableMap(swaggersAndIdentifiers);
    }

    private Map<String, String> getCarRentalAgencySwagger() {
        String body = getRestCallResponse(agencyApiDocUrl);

        String openApiContent = Optional.ofNullable(body)
                .orElseThrow(() -> new CarRentalException("Car Rental Agency swagger is empty"));

        return Map.of(AGENCY, openApiContent);
    }

    private Map<String, String> getCarRentalBookingSwagger() {
        String body = getRestCallResponse(bookingApiDocUrl);

        String openApiContent = Optional.ofNullable(body)
                .orElseThrow(() -> new CarRentalException("Car Rental Booking swagger is empty"));

        return Map.of(BOOKINGS, openApiContent);
    }

    private Map<String, String> getCarRentalCustomerSwagger() {
        String body = getRestCallResponse(customerApiDocUrl);

        String openApiContent = Optional.ofNullable(body)
                .orElseThrow(() -> new CarRentalException("Car Rental Customers swagger is empty"));

        return Map.of(CUSTOMERS, openApiContent);
    }

    private Map<String, String> getCarRentalExpenseSwagger() {
        String body = getRestCallResponse(expenseApiDocUrl);

        String openApiContent = Optional.ofNullable(body)
                .orElseThrow(() -> new CarRentalException("Car Rental Customers swagger is empty"));

        return Map.of(EXPENSE, openApiContent);
    }

    private String getRestCallResponse(String url) {
        return restClient.get()
                .uri(url)
                .header(X_API_KEY, apikey)
                .retrieve()
                .body(String.class);
    }

}
