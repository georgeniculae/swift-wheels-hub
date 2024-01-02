package com.carrental.expense.service;

import com.carrental.dto.BookingClosingDetailsDto;
import com.carrental.dto.BookingDto;
import com.carrental.exception.CarRentalNotFoundException;
import com.carrental.exception.CarRentalResponseStatusException;
import com.carrental.lib.util.HttpRequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookingService {

    private static final String SEPARATOR = "/";

    @Value("${rest-client.url.car-rental-bookings}")
    private String url;

    private final RestClient restClient;

    public BookingDto findBookingById(HttpServletRequest request, Long bookingId) {
        String finalUrl = url + SEPARATOR + bookingId;

        return restClient.get()
                .uri(finalUrl)
                .headers(HttpRequestUtil.mutateHeaders(request))
                .exchange((clientRequest, clientResponse) -> {
                    HttpStatusCode statusCode = clientResponse.getStatusCode();
                    if (statusCode.isError()) {
                        throw new CarRentalResponseStatusException(statusCode, clientResponse.getStatusText());
                    }

                    if (ObjectUtils.isEmpty(clientResponse.bodyTo(BookingDto.class)) ||
                            clientResponse.getBody().available() == 0) {
                        throw new CarRentalNotFoundException("Booking with id: " + bookingId + " not found");
                    }

                    return Objects.requireNonNull(clientResponse.bodyTo(BookingDto.class));
                });
    }

    public void closeBooking(HttpServletRequest request, BookingClosingDetailsDto bookingClosingDetailsDto) {
        String finalUrl = url + SEPARATOR + "close-booking";

        restClient.post()
                .uri(finalUrl)
                .headers(HttpRequestUtil.mutateHeaders(request))
                .body(bookingClosingDetailsDto)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (clientRequest, clientResponse) -> {
                    throw new CarRentalResponseStatusException(clientResponse.getStatusCode(), clientResponse.getStatusText());
                })
                .toBodilessEntity();
    }

}
