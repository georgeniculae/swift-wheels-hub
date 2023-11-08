package com.carrental.expense.service;

import com.carrental.dto.BookingClosingDetailsDto;
import com.carrental.dto.BookingDto;
import com.carrental.lib.exception.CarRentalNotFoundException;
import com.carrental.lib.util.HttpRequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {

    private static final String SEPARATOR = "/";

    @Value("${rest-template.url.car-rental-bookings}")
    private String url;

    private final RestTemplate restTemplate;

    public BookingDto findBookingById(HttpServletRequest request, Long bookingId) {
        String finalUrl = url + SEPARATOR + bookingId;

        HttpEntity<String> httpEntity = HttpRequestUtil.getHttpEntity(request);

        return Optional.ofNullable(restTemplate.exchange(finalUrl, HttpMethod.GET, httpEntity, BookingDto.class).getBody())
                .orElseThrow(() -> new CarRentalNotFoundException("Booking with id: " + bookingId + " not found"));
    }

    public void closeBooking(HttpServletRequest request, BookingClosingDetailsDto bookingClosingDetailsDto) {
        String finalUrl = url + SEPARATOR + "close-booking";

        HttpEntity<Object> httpEntity = HttpRequestUtil.getHttpEntityWithBody(request, bookingClosingDetailsDto);

        restTemplate.exchange(finalUrl, HttpMethod.POST, httpEntity, BookingDto.class);
    }

}
