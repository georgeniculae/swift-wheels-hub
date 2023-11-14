package com.carrental.booking.service;

import com.carrental.dto.CarDetailsForUpdateDto;
import com.carrental.dto.CarDto;
import com.carrental.dto.CarForUpdate;
import com.carrental.entity.CarStatus;
import com.carrental.lib.exception.CarRentalResponseStatusException;
import com.carrental.lib.util.HttpRequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarService {

    public static final String SEPARATOR = "/";

    private static final String CAR_STATUS = "carStatus";

    @Value("${rest-template.url.car-rental-agency-cars}")
    private String url;

    private final RestTemplate restTemplate;

    public CarDto findAvailableCarById(HttpServletRequest request, Long carId) {
        String finalUrl = url + SEPARATOR + carId + SEPARATOR + "availability";

        HttpEntity<String> httpEntity = HttpRequestUtil.getHttpEntity(request);

        return Optional.ofNullable(restTemplate.exchange(finalUrl, HttpMethod.GET, httpEntity, CarDto.class).getBody())
                .orElseThrow(() -> new CarRentalResponseStatusException(HttpStatus.BAD_REQUEST, "No car available"));
    }

    public void changeCarStatus(HttpServletRequest request, Long carId, CarStatus carStatus) {
        String finalUrl = url + SEPARATOR + carId + SEPARATOR + "change-car-status";

        HttpEntity<String> httpEntity = HttpRequestUtil.getHttpEntity(request);

        URI uri = UriComponentsBuilder
                .fromUri(URI.create(finalUrl))
                .queryParam(CAR_STATUS, carStatus.name())
                .build()
                .toUri();

        restTemplate.exchange(uri, HttpMethod.PUT, httpEntity, CarDto.class);
    }

    public void updateCarWhenBookingIsFinished(HttpServletRequest request,
                                               CarDetailsForUpdateDto carDetailsForUpdateDto) {
        String finalUrl = url + SEPARATOR + carDetailsForUpdateDto.carId() + SEPARATOR + "update-after-closed-booking";

        HttpEntity<Object> httpEntity = HttpRequestUtil.getHttpEntityWithBody(request, carDetailsForUpdateDto);

        restTemplate.exchange(finalUrl, HttpMethod.PUT, httpEntity, CarDto.class);
    }

    public void updateCarsStatus(HttpServletRequest request, List<CarForUpdate> carsForUpdate) {
        String finalUrl = url + SEPARATOR + "update-cars-status";

        HttpEntity<Object> httpEntity = HttpRequestUtil.getHttpEntityWithBody(request, carsForUpdate);

        ParameterizedTypeReference<List<CarDto>> responseType = new ParameterizedTypeReference<>() {
        };

        restTemplate.exchange(finalUrl, HttpMethod.PUT, httpEntity, responseType);
    }

}
