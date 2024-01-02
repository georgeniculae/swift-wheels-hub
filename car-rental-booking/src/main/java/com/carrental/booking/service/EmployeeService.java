package com.carrental.booking.service;

import com.carrental.dto.EmployeeDto;
import com.carrental.exception.CarRentalNotFoundException;
import com.carrental.exception.CarRentalResponseStatusException;
import com.carrental.lib.util.HttpRequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    @Value("${rest-client.url.car-rental-agency-employees}")
    private String url;

    private final RestClient restClient;

    public EmployeeDto findEmployeeById(HttpServletRequest request, Long receptionistEmployeeId) {
        return restClient.get()
                .uri(url)
                .headers(HttpRequestUtil.mutateHeaders(request))
                .exchange((clientRequest, clientResponse) -> {
                    HttpStatusCode statusCode = clientResponse.getStatusCode();
                    if (statusCode.isError()) {
                        throw new CarRentalResponseStatusException(statusCode, clientResponse.getStatusText());
                    }

                    if (ObjectUtils.isEmpty(clientResponse.bodyTo(EmployeeDto.class)) ||
                            clientResponse.getBody().available() == 0) {
                        throw new CarRentalNotFoundException("Employee with id: " + receptionistEmployeeId + " not found");
                    }

                    return Objects.requireNonNull(clientResponse.bodyTo(EmployeeDto.class));
                });
    }

}
