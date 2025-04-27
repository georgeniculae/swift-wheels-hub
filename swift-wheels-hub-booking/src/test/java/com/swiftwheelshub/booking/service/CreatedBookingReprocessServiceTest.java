package com.swiftwheelshub.booking.service;

import com.swiftwheelshub.booking.mapper.BookingMapper;
import com.swiftwheelshub.booking.mapper.BookingMapperImpl;
import com.swiftwheelshub.booking.producer.bookingprocessing.BookingProducerService;
import com.swiftwheelshub.booking.producer.bookingprocessing.CreatedBookingCarUpdateProducerService;
import com.swiftwheelshub.booking.util.TestUtil;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.dto.CarStatusUpdate;
import com.swiftwheelshub.dto.CreatedBookingReprocessRequest;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreatedBookingReprocessServiceTest {

    @InjectMocks
    private CreatedBookingReprocessService createdBookingReprocessService;

    @Mock
    private CreatedBookingCarUpdateProducerService createdBookingCarUpdateProducerService;

    @Mock
    private BookingProducerService bookingProducerService;

    @Spy
    private BookingMapper bookingMapper = new BookingMapperImpl();

    @Test
    void reprocessCreatedBookingTest_success() {
        CreatedBookingReprocessRequest reprocessRequest =
                TestUtil.getResourceAsJson("/data/CreatedBookingReprocessRequest.json", CreatedBookingReprocessRequest.class);

        doNothing().when(createdBookingCarUpdateProducerService).changeCarStatus(any(CarStatusUpdate.class));
        doNothing().when(bookingProducerService).sendSavedBooking(any(BookingResponse.class));

        assertDoesNotThrow(() -> createdBookingReprocessService.reprocessCreatedBooking(reprocessRequest));
        verify(bookingMapper).mapReprocessRequestToBookingResponse(any(CreatedBookingReprocessRequest.class));
    }

    @Test
    void reprocessCreatedBookingTest_failedBookingSend() {
        CreatedBookingReprocessRequest reprocessRequest =
                TestUtil.getResourceAsJson("/data/CreatedBookingReprocessRequest.json", CreatedBookingReprocessRequest.class);

        doNothing().when(createdBookingCarUpdateProducerService).changeCarStatus(any(CarStatusUpdate.class));
        doThrow(new RuntimeException("Test")).when(bookingProducerService).sendSavedBooking(any(BookingResponse.class));

        assertThrows(SwiftWheelsHubException.class, () -> createdBookingReprocessService.reprocessCreatedBooking(reprocessRequest));
    }

}
