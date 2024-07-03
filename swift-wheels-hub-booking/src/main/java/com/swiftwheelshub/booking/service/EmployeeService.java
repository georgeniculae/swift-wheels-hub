package com.swiftwheelshub.booking.service;

import com.swiftwheelshub.dto.EmployeeResponse;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshub.lib.util.HttpRequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private static final String SEPARATOR = "/";

    @Value("${rest-client.url.swift-wheels-hub-agency-employees}")
    private String url;

    private final RestClient restClient;

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 5, backoff = @Backoff(value = 5000L),
            listeners = "bookingService"
    )
    public EmployeeResponse findEmployeeById(String apikey, Collection<GrantedAuthority> authorities, Long receptionistEmployeeId) {
        return restClient.get()
                .uri(url + SEPARATOR + receptionistEmployeeId)
                .headers(HttpRequestUtil.setHttpHeaders(apikey, authorities))
                .exchange((_, clientResponse) -> {
                    HttpStatusCode statusCode = clientResponse.getStatusCode();
                    if (statusCode.isError()) {
                        throw new SwiftWheelsHubResponseStatusException(statusCode, clientResponse.getStatusText());
                    }

                    return Optional.ofNullable(clientResponse.bodyTo(EmployeeResponse.class))
                            .orElseThrow(() -> new SwiftWheelsHubNotFoundException("Employee with id: " + receptionistEmployeeId + " not found"));
                });
    }

}
