package com.swiftwheelshub.expense.service;

import com.swiftwheelshub.dto.AuthenticationInfo;
import com.swiftwheelshub.dto.BookingClosingDetails;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.dto.BookingRollbackResponse;
import com.swiftwheelshub.dto.BookingUpdateResponse;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private static final String SEPARATOR = "/";
    private final RestClient restClient;

    @Value("${rest-client.url.swift-wheels-hub-bookings}")
    private String url;

    public BookingResponse findBookingById(AuthenticationInfo authenticationInfo, Long bookingId) {
        String finalUrl = url + SEPARATOR + bookingId;

        return restClient.get()
                .uri(finalUrl)
                .headers(HttpRequestUtil.setHttpHeaders(authenticationInfo.apikey(), authenticationInfo.roles()))
                .exchange((_, clientResponse) -> {
                    HttpStatusCode statusCode = clientResponse.getStatusCode();

                    if (statusCode.isError()) {
                        throw new SwiftWheelsHubResponseStatusException(statusCode, clientResponse.getStatusText());
                    }

                    return Optional.ofNullable(clientResponse.bodyTo(BookingResponse.class))
                            .orElseThrow(() -> new SwiftWheelsHubNotFoundException("Booking with id: " + bookingId + " not found"));
                });
    }

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 5,
            backoff = @Backoff(value = 5000L),
            listeners = "invoiceService"
    )
    public BookingUpdateResponse closeBooking(AuthenticationInfo authenticationInfo, BookingClosingDetails bookingClosingDetails) {
        String finalUrl = url + SEPARATOR + "close-booking";

        return restClient.post()
                .uri(finalUrl)
                .headers(HttpRequestUtil.setHttpHeaders(authenticationInfo.apikey(), authenticationInfo.roles()))
                .body(bookingClosingDetails)
                .exchange((_, clientResponse) -> {
                    if (clientResponse.getStatusCode().isError()) {
                        log.warn("Error occurred while closing booking: {}", clientResponse.getStatusText());

                        return new BookingUpdateResponse(false);
                    }

                    return new BookingUpdateResponse(true);
                });
    }

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 5,
            backoff = @Backoff(value = 5000L),
            listeners = "invoiceService"
    )
    public BookingRollbackResponse rollbackBooking(AuthenticationInfo authenticationInfo, Long bookingId) {
        String finalUrl = url + SEPARATOR + "rollback-booking";

        return restClient.patch()
                .uri(finalUrl)
                .headers(HttpRequestUtil.setHttpHeaders(authenticationInfo.apikey(), authenticationInfo.roles()))
                .body(bookingId)
                .exchange((_, clientResponse) -> {
                    if (clientResponse.getStatusCode().isError()) {
                        log.warn("Error occurred while rolling back booking: {}", clientResponse.getStatusText());

                        return new BookingRollbackResponse(false, bookingId);
                    }

                    return Optional.ofNullable(clientResponse.bodyTo(BookingRollbackResponse.class))
                            .orElse(new BookingRollbackResponse(false, bookingId));
                });
    }

}
