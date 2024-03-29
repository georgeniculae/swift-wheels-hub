package com.swiftwheelshub.booking.service;

import com.swiftwheelshub.dto.EmployeeResponse;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshub.lib.util.HttpRequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private static final String SEPARATOR = "/";

    @Value("${rest-client.url.swift-wheels-hub-agency-employees}")
    private String url;

    private final RestClient restClient;

    public EmployeeResponse findEmployeeById(HttpServletRequest request, Long receptionistEmployeeId) {
        return restClient.get()
                .uri(url + SEPARATOR + receptionistEmployeeId)
                .headers(HttpRequestUtil.mutateHeaders(request))
                .exchange((clientRequest, clientResponse) -> {
                    HttpStatusCode statusCode = clientResponse.getStatusCode();
                    if (statusCode.isError()) {
                        throw new SwiftWheelsHubResponseStatusException(statusCode, clientResponse.getStatusText());
                    }

                    EmployeeResponse employeeResponse = clientResponse.bodyTo(EmployeeResponse.class);

                    if (ObjectUtils.isEmpty(employeeResponse)) {
                        throw new SwiftWheelsHubNotFoundException("Employee with id: " + receptionistEmployeeId + " not found");
                    }

                    return employeeResponse;
                });
    }

}
