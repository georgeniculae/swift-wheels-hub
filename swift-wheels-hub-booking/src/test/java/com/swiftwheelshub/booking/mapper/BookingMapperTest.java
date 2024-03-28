package com.swiftwheelshub.booking.mapper;

import com.swiftwheelshub.booking.util.AssertionUtils;
import com.swiftwheelshub.booking.util.TestUtils;
import com.swiftwheelshub.dto.BookingRequest;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.entity.Booking;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingMapperTest {

    private final BookingMapper bookingMapper = new BookingMapperImpl();

    @Test
    void mapEntityToDtoTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);

        BookingResponse bookingResponse = bookingMapper.mapEntityToDto(booking);

        AssertionUtils.assertBooking(booking, bookingResponse);
    }

    @Test
    void mapDtoToEntityTest_success() {
        BookingRequest bookingRequest = TestUtils.getResourceAsJson("/data/BookingRequest.json", BookingRequest.class);

        Booking actualBooking = bookingMapper.mapDtoToEntity(bookingRequest);

        AssertionUtils.assertBooking(actualBooking, bookingRequest);
    }

}
