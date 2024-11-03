package com.swiftwheelshub.expense.service;

import com.swiftwheelshub.dto.AuthenticationInfo;
import com.swiftwheelshub.dto.CarUpdateDetails;
import com.swiftwheelshub.dto.StatusUpdateResponse;
import com.swiftwheelshub.lib.util.HttpRequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarService {

    private static final String SEPARATOR = "/";
    private final RestClient restClient;

    @Value("${rest-client.url.swift-wheels-hub-agency-cars}")
    private String url;

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 5,
            backoff = @Backoff(value = 5000L),
            listeners = "invoiceService"
    )
    public StatusUpdateResponse markCarAsAvailable(AuthenticationInfo authenticationInfo,
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

}
