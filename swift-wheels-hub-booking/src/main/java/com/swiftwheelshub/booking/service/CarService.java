package com.swiftwheelshub.booking.service;

import com.swiftwheelshub.dto.AuthenticationInfo;
import com.swiftwheelshub.dto.CarResponse;
import com.swiftwheelshub.dto.CarState;
import com.swiftwheelshub.dto.CarUpdateDetails;
import com.swiftwheelshub.dto.StatusUpdateResponse;
import com.swiftwheelshub.dto.UpdateCarRequest;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshub.lib.util.HttpRequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class CarService {

    private static final String SEPARATOR = "/";
    private static final String CAR_STATE = "carState";
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
    public StatusUpdateResponse changeCarStatus(AuthenticationInfo authenticationInfo, Long carId, CarState carState) {
        String finalUrl = url + SEPARATOR + carId + SEPARATOR + "change-status";

        URI uri = UriComponentsBuilder
                .fromUri(URI.create(finalUrl))
                .queryParam(CAR_STATE, carState.name())
                .build()
                .toUri();

        return restClient.patch()
                .uri(uri)
                .headers(HttpRequestUtil.setHttpHeaders(authenticationInfo.apikey(), authenticationInfo.roles()))
                .exchange((_, clientResponse) -> {
                    if (clientResponse.getStatusCode().isError()) {
                        log.warn("Error occurred while changing car status: {}", clientResponse.getStatusText());

                        return new StatusUpdateResponse(false);
                    }

                    return new StatusUpdateResponse(true);
                });
    }

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 5,
            backoff = @Backoff(value = 5000L),
            listeners = "bookingService"
    )
    public StatusUpdateResponse updateCarWhenBookingIsFinished(AuthenticationInfo authenticationInfo,
                                                               CarUpdateDetails carUpdateDetails) {
        String finalUrl = url + SEPARATOR + carUpdateDetails.carId() + SEPARATOR + "update-after-return";

        return restClient.put()
                .uri(finalUrl)
                .headers(HttpRequestUtil.setHttpHeaders(authenticationInfo.apikey(), authenticationInfo.roles()))
                .body(carUpdateDetails)
                .exchange((_, clientResponse) -> {
                    if (clientResponse.getStatusCode().isError()) {
                        log.warn("Error occurred while updating car status: {}", clientResponse.getStatusText());

                        return new StatusUpdateResponse(false);
                    }

                    return new StatusUpdateResponse(true);
                });
    }

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 5,
            backoff = @Backoff(value = 5000L),
            listeners = "bookingService"
    )
    public StatusUpdateResponse updateCarsStatuses(AuthenticationInfo authenticationInfo, List<UpdateCarRequest> carsForUpdate) {
        String finalUrl = url + SEPARATOR + "update-statuses";

        return restClient.put()
                .uri(finalUrl)
                .headers(HttpRequestUtil.setHttpHeaders(authenticationInfo.apikey(), authenticationInfo.roles()))
                .body(carsForUpdate)
                .exchange((_, clientResponse) -> {
                    if (clientResponse.getStatusCode().isError()) {
                        log.warn("Error occurred while updating cars statuses: {}", clientResponse.getStatusText());

                        return new StatusUpdateResponse(false);
                    }

                    return new StatusUpdateResponse(true);
                });
    }

}
