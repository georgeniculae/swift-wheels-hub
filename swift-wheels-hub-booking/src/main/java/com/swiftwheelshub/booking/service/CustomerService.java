package com.swiftwheelshub.booking.service;

import com.swiftwheelshub.dto.AuthenticationInfo;
import com.swiftwheelshub.dto.UserInfo;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshub.lib.util.HttpRequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private static final String SEPARATOR = "/";

    @Value("${rest-client.url.swift-wheels-hub-customers}")
    private String url;

    private final RestClient restClient;

    public UserInfo getUserByUsername(AuthenticationInfo authenticationInfo) {
        String username = authenticationInfo.username();

        return restClient.get()
                .uri(url + SEPARATOR + "{username}", username)
                .headers(HttpRequestUtil.setHttpHeaders(authenticationInfo.apikey(), authenticationInfo.roles()))
                .exchange((_, clientResponse) -> {
                    HttpStatusCode statusCode = clientResponse.getStatusCode();

                    if (statusCode.is5xxServerError()) {
                        throw new SwiftWheelsHubResponseStatusException(statusCode, clientResponse.getStatusText());
                    }

                    return Optional.ofNullable(clientResponse.bodyTo(UserInfo.class))
                            .orElseThrow(() -> new SwiftWheelsHubNotFoundException("User with username: " + username + " not found"));
                });
    }

}
