package com.swiftwheelshub.expense.service;

import com.swiftwheelshub.dto.BookingClosingDetails;
import com.swiftwheelshub.dto.BookingResponse;
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
public class BookingService {

    private static final String SEPARATOR = "/";

    @Value("${rest-client.url.swift-wheels-hub-bookings}")
    private String url;

    private final RestClient restClient;

    public BookingResponse findBookingById(String apikey, Collection<GrantedAuthority> authorities, Long bookingId) {
        String finalUrl = url + SEPARATOR + bookingId;

        return restClient.get()
                .uri(finalUrl)
                .headers(HttpRequestUtil.setHttpHeaders(apikey, authorities))
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
    public void closeBooking(String apikey, Collection<GrantedAuthority> authorities, BookingClosingDetails bookingClosingDetails) {
        String finalUrl = url + SEPARATOR + "close-booking";

        restClient.post()
                .uri(finalUrl)
                .headers(HttpRequestUtil.setHttpHeaders(apikey, authorities))
                .body(bookingClosingDetails)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, clientResponse) -> {
                    throw new SwiftWheelsHubResponseStatusException(clientResponse.getStatusCode(), clientResponse.getStatusText());
                })
                .toBodilessEntity();
    }

}
