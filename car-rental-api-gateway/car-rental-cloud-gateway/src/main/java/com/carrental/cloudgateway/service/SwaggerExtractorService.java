package com.carrental.cloudgateway.service;

import com.carrental.cloudgateway.exception.CarRentalException;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple4;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "swagger-validator", name = "enabled", havingValue = "true")
@Slf4j
public class SwaggerExtractorService {

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

    private static final String X_API_KEY = "X-API-KEY";

    private static final String HYPHEN_REGEX = "-";

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
    private Map<String, String> getSwaggersIdentifiersAndContents(Tuple4<Map<String, String>, Map<String, String>, Map<String, String>, Map<String, String>> swaggersTuple) {
        Map<String, String> swaggers = new HashMap<>();

        swaggersTuple.forEach(o -> swaggers.putAll((Map<String, String>) o));

        return swaggers;
    }

    private Mono<Map<String, String>> addSwaggerIdentifierAndContent(Map<String, String> swaggerIdentifierAndContent) {
        return getCarRentalBookingSwagger()
                .map(actualIdentifierAndContent -> {
                    swaggerIdentifierAndContent.putAll(actualIdentifierAndContent);

                    return swaggerIdentifierAndContent;
                });
    }

    private Mono<Map<String, String>> getCarRentalAgencySwagger() {
        return webClient.get()
                .uri(agencyApiDocUrl)
                .header(X_API_KEY, apikey)
                .retrieve()
                .bodyToMono(String.class)
                .flux()
                .collectMap(openAPI -> "agency");
    }

    private Mono<Map<String, String>> getCarRentalBookingSwagger() {
        return webClient.get()
                .uri(bookingApiDocUrl)
                .header(X_API_KEY, apikey)
                .retrieve()
                .bodyToMono(String.class)
                .flux()
                .collectMap(openAPI -> "bookings");
    }

    private Mono<Map<String, String>> getCarRentalCustomerSwagger() {
        return webClient.get()
                .uri(customerApiDocUrl)
                .header(X_API_KEY, apikey)
                .retrieve()
                .bodyToMono(String.class)
                .flux()
                .collectMap(openAPI -> "customers");
    }

    private Mono<Map<String, String>> getCarRentalExpenseSwagger() {
        return webClient.get()
                .uri(expenseApiDocUrl)
                .header(X_API_KEY, apikey)
                .retrieve()
                .bodyToMono(String.class)
                .flux()
                .collectMap(openAPI -> "expense");
    }

    private Map<String, OpenAPI> getSwaggerFiles(Resource resource) {
        return getFilesPaths(resource).stream()
                .collect(Collectors.toMap(this::getSwaggerIdentifier, this::extractSwaggerFile));
    }

    private List<Path> getFilesPaths(Resource resource) {
        try {
            Path path = Paths.get(resource.getURI());

            try (Stream<Path> lines = Files.list(path)) {
                return lines.toList();
            }
        } catch (Exception e) {
            throw new CarRentalException(e);
        }
    }

    private String getSwaggerIdentifier(Path swaggerFilePath) {
        String[] split = FilenameUtils.removeExtension(swaggerFilePath.getFileName().toString()).split(HYPHEN_REGEX);

        return split[split.length - 1];
    }

    private OpenAPI extractSwaggerFile(Path swaggerFilePath) {
        return new OpenAPIV3Parser().read(swaggerFilePath.toUri().toString());
    }

}
