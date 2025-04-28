package com.autohub.booking.mapper;

import com.autohub.booking.util.AssertionUtil;
import com.autohub.booking.util.TestUtil;
import com.autohub.dto.BookingRequest;
import com.autohub.dto.BookingResponse;
import com.autohub.entity.Booking;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingMapperTest {

    private final BookingMapper bookingMapper = new BookingMapperImpl();

    @Test
    void mapEntityToDtoTest_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);

        BookingResponse bookingResponse = bookingMapper.mapEntityToDto(booking);

        AssertionUtil.assertBooking(booking, bookingResponse);
    }

    @Test
    void mapDtoToEntityTest_success() {
        BookingRequest bookingRequest =
                TestUtil.getResourceAsJson("/data/BookingRequest.json", BookingRequest.class);

        Booking actualBooking = bookingMapper.mapDtoToEntity(bookingRequest);

        AssertionUtil.assertBooking(actualBooking, bookingRequest);
    }

}
