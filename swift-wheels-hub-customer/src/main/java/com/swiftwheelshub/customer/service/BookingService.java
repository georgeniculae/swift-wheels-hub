package com.swiftwheelshub.customer.service;

import com.swiftwheelshub.dto.AuthenticationInfo;
import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshub.lib.util.HttpRequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class BookingService {

    private static final String SEPARATOR = "/";

    @Value("${booking-service.url}")
    private String url;

    private final RestClient restClient;

    public void deleteBookingsByUsername(AuthenticationInfo authenticationInfo, String username) {
        restClient.delete()
                .uri(url + SEPARATOR + username)
                .headers(HttpRequestUtil.setHttpHeaders(authenticationInfo.apikey(), authenticationInfo.roles()))
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, clientResponse) -> {
                    throw new SwiftWheelsHubResponseStatusException(clientResponse.getStatusCode(), clientResponse.getStatusText());
                })
                .toBodilessEntity();
    }

}
