package com.carrental.cloudgateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple4;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "swagger-validator", name = "enabled", havingValue = "true")
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

    private final WebClient webClient;

    public Mono<Map<String, String>> getSwaggerIdentifierAndContent() {
        return Mono.zip(
                        getCarRentalAgencySwagger(),
                        getCarRentalBookingSwagger(),
                        getCarRentalCustomerSwagger(),
                        getCarRentalExpenseSwagger()
                )
                .map(this::getSwaggersIdentifiersAndContents);
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getSwaggersIdentifiersAndContents(Tuple4<Map<String, String>, Map<String, String>, Map<String, String>, Map<String, String>> swaggersMapTuple) {
        Map<String, String> swaggers = new HashMap<>();

        swaggersMapTuple.forEach(o -> swaggers.putAll((Map<String, String>) o));

        return swaggers;
    }

    private Mono<Map<String, String>> getCarRentalAgencySwagger() {
        return webClient.get()
                .uri(agencyApiDocUrl)
                .header(X_API_KEY, apikey)
                .retrieve()
                .bodyToMono(String.class)
                .flux()
                .collectMap(openAPI -> AGENCY);
    }

    private Mono<Map<String, String>> getCarRentalBookingSwagger() {
        return webClient.get()
                .uri(bookingApiDocUrl)
                .header(X_API_KEY, apikey)
                .retrieve()
                .bodyToMono(String.class)
                .flux()
                .collectMap(openAPI -> BOOKINGS);
    }

    private Mono<Map<String, String>> getCarRentalCustomerSwagger() {
        return webClient.get()
                .uri(customerApiDocUrl)
                .header(X_API_KEY, apikey)
                .retrieve()
                .bodyToMono(String.class)
                .flux()
                .collectMap(openAPI -> CUSTOMERS);
    }

    private Mono<Map<String, String>> getCarRentalExpenseSwagger() {
        return webClient.get()
                .uri(expenseApiDocUrl)
                .header(X_API_KEY, apikey)
                .retrieve()
                .bodyToMono(String.class)
                .flux()
                .collectMap(openAPI -> EXPENSE);
    }

}
