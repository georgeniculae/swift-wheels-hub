package com.swiftwheelshub.expense.service;

import com.swiftwheelshub.dto.BookingClosingDetails;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshub.lib.util.HttpRequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class BookingService {

    private static final String SEPARATOR = "/";

    @Value("${rest-client.url.swift-wheels-hub-bookings}")
    private String url;

    private final RestClient restClient;

    public BookingResponse findBookingById(HttpServletRequest request, Long bookingId) {
        String finalUrl = url + SEPARATOR + bookingId;

        return restClient.get()
                .uri(finalUrl)
                .headers(HttpRequestUtil.mutateHeaders(request))
                .exchange((clientRequest, clientResponse) -> {
                    HttpStatusCode statusCode = clientResponse.getStatusCode();
                    if (statusCode.isError()) {
                        throw new SwiftWheelsHubResponseStatusException(statusCode, clientResponse.getStatusText());
                    }

                    BookingResponse bookingResponse = clientResponse.bodyTo(BookingResponse.class);

                    if (ObjectUtils.isEmpty(bookingResponse)) {
                        throw new SwiftWheelsHubNotFoundException("Booking with id: " + bookingId + " not found");
                    }

                    return bookingResponse;
                });
    }

    public void closeBooking(HttpServletRequest request, BookingClosingDetails bookingClosingDetails) {
        String finalUrl = url + SEPARATOR + "close-booking";

        restClient.post()
                .uri(finalUrl)
                .headers(HttpRequestUtil.mutateHeaders(request))
                .body(bookingClosingDetails)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (clientRequest, clientResponse) -> {
                    throw new SwiftWheelsHubResponseStatusException(clientResponse.getStatusCode(), clientResponse.getStatusText());
                })
                .toBodilessEntity();
    }

}
