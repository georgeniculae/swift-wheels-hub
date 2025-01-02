package com.swiftwheelshub.booking.service;

import com.swiftwheelshub.dto.AuthenticationInfo;
import com.swiftwheelshub.dto.CarResponse;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshub.lib.util.HttpRequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarService {

    private static final String SEPARATOR = "/";
    private final RestClient restClient;

    @Value("${rest-client.url.swift-wheels-hub-agency-cars}")
    private String url;

    public CarResponse findAvailableCarById(AuthenticationInfo authenticationInfo, Long carId) {
        String finalUrl = url + SEPARATOR + carId + SEPARATOR + "availability";

        return restClient.get()
                .uri(finalUrl)
                .headers(HttpRequestUtil.setHttpHeaders(authenticationInfo.apikey(), authenticationInfo.roles()))
                .exchange((_, clientResponse) -> {
                    HttpStatusCode statusCode = clientResponse.getStatusCode();

                    if (statusCode.isError()) {
                        throw new SwiftWheelsHubResponseStatusException(statusCode, clientResponse.getStatusText());
                    }

                    return Optional.ofNullable(clientResponse.bodyTo(CarResponse.class))
                            .orElseThrow(() -> new SwiftWheelsHubNotFoundException("Car with id: " + carId + " not found"));
                });
    }

}
