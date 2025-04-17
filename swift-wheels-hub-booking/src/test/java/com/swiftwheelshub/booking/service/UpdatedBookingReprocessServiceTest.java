package com.swiftwheelshub.booking.service;

import com.swiftwheelshub.booking.mapper.BookingMapper;
import com.swiftwheelshub.booking.mapper.BookingMapperImpl;
import com.swiftwheelshub.booking.producer.bookingprocessing.BookingProducerService;
import com.swiftwheelshub.booking.producer.bookingprocessing.UpdateBookingUpdateCarsProducerService;
import com.swiftwheelshub.booking.util.TestUtil;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.dto.UpdateCarsRequest;
import com.swiftwheelshub.dto.UpdatedBookingReprocessRequest;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        when(updateBookingUpdateCarsProducerService.updateCarsStatus(any(UpdateCarsRequest.class))).thenReturn(true);
        when(bookingProducerService.sendUpdatedBooking(any(BookingResponse.class))).thenReturn(true);

        assertDoesNotThrow(() -> updatedBookingReprocessService.reprocessUpdatedBooking(reprocessRequest));
        verify(bookingMapper).mapReprocessRequestToBookingResponse(any(UpdatedBookingReprocessRequest.class));
    }

    @Test
    void reprocessUpdatedBookingTest_failedBookingSend() {
        UpdatedBookingReprocessRequest reprocessRequest =
                TestUtil.getResourceAsJson("/data/UpdatedBookingReprocessRequest.json", UpdatedBookingReprocessRequest.class);

        when(updateBookingUpdateCarsProducerService.updateCarsStatus(any(UpdateCarsRequest.class))).thenReturn(true);
        when(bookingProducerService.sendUpdatedBooking(any(BookingResponse.class))).thenReturn(false);

        assertThrows(SwiftWheelsHubException.class, () -> updatedBookingReprocessService.reprocessUpdatedBooking(reprocessRequest));
    }

}
