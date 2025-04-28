package com.autohub.booking.service;

import com.autohub.booking.mapper.BookingMapper;
import com.autohub.booking.mapper.BookingMapperImpl;
import com.autohub.booking.producer.bookingprocessing.BookingProducerService;
import com.autohub.booking.producer.bookingprocessing.UpdateBookingUpdateCarsProducerService;
import com.autohub.booking.util.TestUtil;
import com.autohub.dto.BookingResponse;
import com.autohub.dto.UpdateCarsRequest;
import com.autohub.dto.UpdatedBookingReprocessRequest;
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
class UpdatedBookingReprocessServiceTest {

    @InjectMocks
    private UpdatedBookingReprocessService updatedBookingReprocessService;

    @Mock
    private UpdateBookingUpdateCarsProducerService updateBookingUpdateCarsProducerService;

    @Mock
    private BookingProducerService bookingProducerService;

    @Spy
    private BookingMapper bookingMapper = new BookingMapperImpl();

    @Test
    void reprocessUpdatedBookingTest_success() {
        UpdatedBookingReprocessRequest reprocessRequest =
                TestUtil.getResourceAsJson("/data/UpdatedBookingReprocessRequest.json", UpdatedBookingReprocessRequest.class);

        doNothing().when(updateBookingUpdateCarsProducerService).updateCarsStatus(any(UpdateCarsRequest.class));
        doNothing().when(bookingProducerService).sendUpdatedBooking(any(BookingResponse.class));

        assertDoesNotThrow(() -> updatedBookingReprocessService.reprocessUpdatedBooking(reprocessRequest));
        verify(bookingMapper).mapReprocessRequestToBookingResponse(any(UpdatedBookingReprocessRequest.class));
    }

    @Test
    void reprocessUpdatedBookingTest_failedBookingSend() {
        UpdatedBookingReprocessRequest reprocessRequest =
                TestUtil.getResourceAsJson("/data/UpdatedBookingReprocessRequest.json", UpdatedBookingReprocessRequest.class);

        doNothing().when(updateBookingUpdateCarsProducerService).updateCarsStatus(any(UpdateCarsRequest.class));
        doThrow(new RuntimeException("Test")).when(bookingProducerService).sendUpdatedBooking(any(BookingResponse.class));

        assertThrows(AutoHubException.class, () -> updatedBookingReprocessService.reprocessUpdatedBooking(reprocessRequest));
    }

}
