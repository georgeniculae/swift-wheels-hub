package com.swiftwheelshub.booking.service;

import com.swiftwheelshub.dto.AuthenticationInfo;
import com.swiftwheelshub.dto.CarResponse;
import com.swiftwheelshub.dto.CarState;
import com.swiftwheelshub.dto.CarUpdateDetails;
import com.swiftwheelshub.dto.UpdateCarRequest;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshub.lib.util.HttpRequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarService {

    private static final String SEPARATOR = "/";
    private static final String CAR_STATUS = "carState";
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

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 5,
            backoff = @Backoff(value = 5000L),
            listeners = "bookingService"
    )
    public void changeCarStatus(AuthenticationInfo authenticationInfo, Long carId, CarState carState) {
        String finalUrl = url + SEPARATOR + carId + SEPARATOR + "change-status";

        URI uri = UriComponentsBuilder
                .fromUri(URI.create(finalUrl))
                .queryParam(CAR_STATUS, carState.name())
                .build()
                .toUri();

        restClient.patch()
                .uri(uri)
                .headers(HttpRequestUtil.setHttpHeaders(authenticationInfo.apikey(), authenticationInfo.roles()))
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, clientResponse) -> {
                    throw new SwiftWheelsHubResponseStatusException(clientResponse.getStatusCode(), clientResponse.getStatusText());
                })
                .toBodilessEntity();
    }

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 5,
            backoff = @Backoff(value = 5000L),
            listeners = "bookingService"
    )
    public void updateCarWhenBookingIsFinished(AuthenticationInfo authenticationInfo,
                                               CarUpdateDetails carUpdateDetails) {
        String finalUrl = url + SEPARATOR + carUpdateDetails.carId() + SEPARATOR + "update-after-return";

        restClient.put()
                .uri(finalUrl)
                .headers(HttpRequestUtil.setHttpHeaders(authenticationInfo.apikey(), authenticationInfo.roles()))
                .body(carUpdateDetails)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, clientResponse) -> {
                    throw new SwiftWheelsHubResponseStatusException(clientResponse.getStatusCode(), clientResponse.getStatusText());
                })
                .toBodilessEntity();
    }

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 5,
            backoff = @Backoff(value = 5000L),
            listeners = "bookingService"
    )
    public void updateCarsStatus(AuthenticationInfo authenticationInfo, List<UpdateCarRequest> carsForUpdate) {
        String finalUrl = url + SEPARATOR + "update-statuses";

        restClient.put()
                .uri(finalUrl)
                .headers(HttpRequestUtil.setHttpHeaders(authenticationInfo.apikey(), authenticationInfo.roles()))
                .body(carsForUpdate)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, clientResponse) -> {
                    throw new SwiftWheelsHubResponseStatusException(clientResponse.getStatusCode(), clientResponse.getStatusText());
                })
                .toBodilessEntity();
    }

}
