package com.swiftwheelshub.ai.service;

import com.swiftwheelshub.dto.AuthenticationInfo;
import com.swiftwheelshub.dto.CarResponse;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshub.lib.util.HttpRequestUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {

    private static final String SEPARATOR = "/";

    @Value("${rest-client.url.swift-wheels-hub-agency-cars}")
    private String url;

    private final RestClient restClient;

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 5,
            backoff = @Backoff(value = 5000L),
            listeners = "carSuggestionService"
    )
    public List<CarResponse> getAllAvailableCars(AuthenticationInfo authenticationInfo) {
        return restClient.get()
                .uri(url + SEPARATOR + "available")
                .headers(HttpRequestUtil.setHttpHeaders(authenticationInfo.apikey(), authenticationInfo.roles()))
                .exchange((request, clientResponse) -> {
                    HttpStatusCode statusCode = clientResponse.getStatusCode();
                    String path = request.getURI().getPath();

                    if (statusCode.isSameCodeAs(HttpStatus.NOT_FOUND)) {
                        throw new SwiftWheelsHubNotFoundException("Path: " + path + " not found");
                    }

                    if (statusCode.isError()) {
                        throw new SwiftWheelsHubResponseStatusException(statusCode, clientResponse.getStatusText());
                    }

                    List<CarResponse> cars = clientResponse.bodyTo(new ParameterizedTypeReference<>() {
                    });

                    if (ObjectUtils.isEmpty(cars)) {
                        throw new SwiftWheelsHubException("No car available at this moment");
                    }

                    return cars;
                });
    }

}
