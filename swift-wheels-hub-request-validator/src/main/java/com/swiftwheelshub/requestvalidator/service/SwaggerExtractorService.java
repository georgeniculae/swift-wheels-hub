package com.swiftwheelshub.requestvalidator.service;

import com.swiftwheelshub.exception.SwiftWheelsHubException;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshub.requestvalidator.config.RegisteredEndpoints;
import com.swiftwheelshub.requestvalidator.model.SwaggerFile;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SwaggerExtractorService {

    private final RestClient restClient;
    private final RegisteredEndpoints registeredEndpoints;

    public List<SwaggerFile> getSwaggerFiles() {
        return registeredEndpoints.getEndpoints()
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

    private SwaggerFile getSwaggerFile(RegisteredEndpoints.RegisteredEndpoint registeredEndpoint) {
        String identifier = registeredEndpoint.getIdentifier();
        String swaggerContent = getRestCallResponse(identifier, registeredEndpoint.getUrl());

        return SwaggerFile.builder()
                .identifier(identifier)
                .swaggerContent(swaggerContent)
                .build();
    }

    private String getRestCallResponse(String microservice, String url) {
        String body = restClient.get()
                .uri(url)
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
