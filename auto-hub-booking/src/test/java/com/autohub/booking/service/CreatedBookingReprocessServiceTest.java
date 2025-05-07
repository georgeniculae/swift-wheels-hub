package com.autohub.booking.service;

import com.autohub.booking.mapper.BookingMapper;
import com.autohub.booking.mapper.BookingMapperImpl;
import com.autohub.booking.producer.bookingprocessing.BookingProducerService;
import com.autohub.booking.producer.bookingprocessing.CreatedBookingCarUpdateProducerService;
import com.autohub.booking.util.TestUtil;
import com.autohub.dto.booking.CreatedBookingReprocessRequest;
import com.autohub.dto.common.BookingResponse;
import com.autohub.dto.common.CarStatusUpdate;
import com.autohub.exception.AutoHubException;
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

        assertThrows(AutoHubException.class, () -> createdBookingReprocessService.reprocessCreatedBooking(reprocessRequest));
    }

}
