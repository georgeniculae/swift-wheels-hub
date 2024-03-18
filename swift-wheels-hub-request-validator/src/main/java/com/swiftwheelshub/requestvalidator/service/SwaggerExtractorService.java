package com.swiftwheelshub.requestvalidator.service;

import com.swiftwheelshub.exception.SwiftWheelsHubException;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.requestvalidator.config.RegisteredEndpoints;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SwaggerExtractorService {

    private static final String X_API_KEY = "X-API-KEY";

    @Value("${apikey.secret}")
    private String apikey;

    private final RestClient restClient;

    private final RegisteredEndpoints registeredEndpoints;

    public List<Pair<String, String>> getSwaggerIdentifierAndContent() {
        return registeredEndpoints.getEndpoints()
                .entrySet()
                .stream()
                .map(endpoints -> {
                    String swaggerContent = getRestCallResponse(endpoints.getKey(), endpoints.getValue());

                    return Pair.of(endpoints.getKey(), swaggerContent);
                })
                .toList();
    }

    public Pair<String, String> getSwaggerFileForMicroservice(String microserviceName) {
        return getSwaggerIdentifierAndContent()
                .stream()
                .filter(swaggerIdentifierAndContent -> microserviceName.contains(swaggerIdentifierAndContent.getFirst()))
                .findFirst()
                .orElseThrow(() -> new SwiftWheelsHubException("Microservice not existent"));
    }

    private String getRestCallResponse(String microservice, String url) {
        String body = restClient.get()
                .uri(url)
                .header(X_API_KEY, apikey)
                .retrieve()
                .body(String.class);

        return Optional.ofNullable(body)
                .filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new SwiftWheelsHubNotFoundException("Swagger content for " + microservice + " is empty"));
    }

}
