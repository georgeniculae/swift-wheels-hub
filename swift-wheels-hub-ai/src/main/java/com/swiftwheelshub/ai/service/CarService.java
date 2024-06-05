package com.swiftwheelshub.ai.service;

import com.swiftwheelshub.dto.CarResponse;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshub.lib.util.HttpRequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
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

    public List<CarResponse> getAllAvailableCars(HttpServletRequest request) {
        return restClient.get()
                .uri(url + SEPARATOR + "available")
                .headers(HttpRequestUtil.mutateHeaders(request))
                .exchange((_, clientResponse) -> {
                    HttpStatusCode statusCode = clientResponse.getStatusCode();
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
