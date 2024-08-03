package com.swiftwheelshub.requestvalidator.service;

import com.swiftwheelshub.exception.SwiftWheelsHubException;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshub.requestvalidator.config.RegisteredEndpoints;
import com.swiftwheelshub.requestvalidator.model.SwaggerFile;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SwaggerExtractorService {

    private static final String X_API_KEY = "X-API-KEY";

    @Value("${apikey.secret}")
    private String apikey;

    private final RestClient restClient;

    private final RegisteredEndpoints registeredEndpoints;

    public List<SwaggerFile> getSwaggerFiles() {
        return registeredEndpoints.getEndpoints()
                .entrySet()
                .stream()
                .map(this::getSwaggerFile)
                .toList();
    }

    public SwaggerFile getSwaggerFileForMicroservice(String microserviceName) {
        return getSwaggerFiles()
                .stream()
                .filter(swaggerFile -> microserviceName.contains(swaggerFile.getIdentifier()))
                .findFirst()
                .orElseThrow(() -> new SwiftWheelsHubNotFoundException("Microservice not existent"));
    }

    private SwaggerFile getSwaggerFile(Map.Entry<String, String> endpoints) {
        String swaggerContent = getRestCallResponse(endpoints.getKey(), endpoints.getValue());

        return SwaggerFile.builder()
                .identifier(endpoints.getKey())
                .swaggerContent(swaggerContent)
                .build();
    }

    private String getRestCallResponse(String microservice, String url) {
        String body = restClient.get()
                .uri(url)
                .header(X_API_KEY, apikey)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, clientResponse) -> {
                    throw new SwiftWheelsHubResponseStatusException(clientResponse.getStatusCode(), clientResponse.getStatusText());
                })
                .body(String.class);

        return Optional.ofNullable(body)
                .filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new SwiftWheelsHubException("Swagger content for " + microservice + " is empty"));
    }

}
