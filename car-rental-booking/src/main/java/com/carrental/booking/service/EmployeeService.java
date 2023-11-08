package com.carrental.booking.service;

import com.carrental.dto.EmployeeDto;
import com.carrental.lib.exception.CarRentalNotFoundException;
import com.carrental.lib.util.HttpRequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    public static final String SEPARATOR = "/";

    @Value("${rest-template.url.car-rental-agency-employees}")
    private String url;

    private final RestTemplate restTemplate;

    public EmployeeDto findEmployeeById(HttpServletRequest request, Long receptionistEmployeeId) {
        HttpEntity<String> httpEntity = HttpRequestUtil.getHttpEntity(request);

        return Optional.ofNullable(restTemplate.exchange(url + SEPARATOR + receptionistEmployeeId, HttpMethod.GET, httpEntity, EmployeeDto.class).getBody())
                .orElseThrow(() -> new CarRentalNotFoundException("Employee with id: " + receptionistEmployeeId + " not found"));
    }

}
