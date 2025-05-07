package com.autohub.booking.util;

import com.autohub.dto.BookingRequest;
import com.autohub.dto.BookingResponse;
import com.autohub.entity.booking.Booking;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtil {

    public static void assertBooking(Booking booking, BookingRequest bookingRequest) {
        assertEquals(booking.getDateFrom(), bookingRequest.dateFrom());
        assertEquals(booking.getDateTo(), bookingRequest.dateTo());
    }

    public static void assertBooking(Booking booking, BookingResponse bookingResponse) {
        assertEquals(booking.getDateOfBooking(), bookingResponse.dateOfBooking());
        assertEquals(booking.getDateFrom(), bookingResponse.dateFrom());
        assertEquals(booking.getDateTo(), bookingResponse.dateTo());
    }

}
