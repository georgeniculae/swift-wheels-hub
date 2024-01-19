package com.swiftwheelshub.booking.service;

import com.swiftwheelshub.dto.CarDetailsForUpdateDto;
import com.swiftwheelshub.dto.CarDto;
import com.swiftwheelshub.dto.CarForUpdate;
import com.swiftwheelshub.entity.CarStatus;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshub.lib.util.HttpRequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CarService {

    private static final String SEPARATOR = "/";

    private static final String CAR_STATUS = "carStatus";

    @Value("${rest-client.url.swift-wheels-hub-agency-cars}")
    private String url;

    private final RestClient restClient;

    public CarDto findAvailableCarById(HttpServletRequest request, Long carId) {
        String finalUrl = url + SEPARATOR + carId + SEPARATOR + "availability";

        return restClient.get()
                .uri(finalUrl)
                .headers(HttpRequestUtil.mutateHeaders(request))
                .exchange((clientRequest, clientResponse) -> {
                    HttpStatusCode statusCode = clientResponse.getStatusCode();
                    if (statusCode.isError()) {
                        throw new SwiftWheelsHubResponseStatusException(statusCode, clientResponse.getStatusText());
                    }

                    if (ObjectUtils.isEmpty(clientResponse.bodyTo(CarDto.class)) ||
                            clientResponse.getBody().available() == 0) {
                        throw new SwiftWheelsHubNotFoundException("Car with id: " + carId + " not found");
                    }

                    return Objects.requireNonNull(clientResponse.bodyTo(CarDto.class));
                });
    }

    public void changeCarStatus(HttpServletRequest request, Long carId, CarStatus carStatus) {
        String finalUrl = url + SEPARATOR + carId + SEPARATOR + "change-car-status";

        URI uri = UriComponentsBuilder
                .fromUri(URI.create(finalUrl))
                .queryParam(CAR_STATUS, carStatus.name())
                .build()
                .toUri();

        restClient.post()
                .uri(uri)
                .headers(HttpRequestUtil.mutateHeaders(request))
                .retrieve()
                .onStatus(HttpStatusCode::isError, (clientRequest, clientResponse) -> {
                    throw new SwiftWheelsHubResponseStatusException(clientResponse.getStatusCode(), clientResponse.getStatusText());
                })
                .toBodilessEntity();
    }

    public void updateCarWhenBookingIsFinished(HttpServletRequest request,
                                               CarDetailsForUpdateDto carDetailsForUpdateDto) {
        String finalUrl = url + SEPARATOR + carDetailsForUpdateDto.carId() + SEPARATOR + "update-after-closed-booking";

        restClient.put()
                .uri(finalUrl)
                .headers(HttpRequestUtil.mutateHeaders(request))
                .body(carDetailsForUpdateDto)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (clientRequest, clientResponse) -> {
                    throw new SwiftWheelsHubResponseStatusException(clientResponse.getStatusCode(), clientResponse.getStatusText());
                })
                .toBodilessEntity();
    }

    public void updateCarsStatus(HttpServletRequest request, List<CarForUpdate> carsForUpdate) {
        String finalUrl = url + SEPARATOR + "update-cars-status";

        restClient.put()
                .uri(finalUrl)
                .headers(HttpRequestUtil.mutateHeaders(request))
                .body(carsForUpdate)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (clientRequest, clientResponse) -> {
                    throw new SwiftWheelsHubResponseStatusException(clientResponse.getStatusCode(), clientResponse.getStatusText());
                })
                .toBodilessEntity();
    }

}
