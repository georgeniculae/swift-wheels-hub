package com.swiftwheelshub.booking.eventhandling;

import com.swiftwheelshub.booking.mapper.BookingMapper;
import com.swiftwheelshub.booking.mapper.BookingMapperImpl;
import com.swiftwheelshub.booking.producer.bookingprocessing.BookingProducerService;
import com.swiftwheelshub.booking.producer.bookingprocessing.UpdateBookingUpdateCarsProducerService;
import com.swiftwheelshub.booking.producer.dlq.FailedUpdatedBookingDlqProducerService;
import com.swiftwheelshub.booking.util.TestUtil;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.dto.UpdateCarsRequest;
import com.swiftwheelshub.dto.UpdatedBookingReprocessRequest;
import com.swiftwheelshub.entity.Booking;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdatedBookingProcessorServiceTest {

    @InjectMocks
    private UpdatedBookingProcessorService updatedBookingProcessorService;

    @Mock
    private UpdateBookingUpdateCarsProducerService updateBookingUpdateCarsProducerService;

    @Mock
    private BookingProducerService bookingProducerService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private FailedUpdatedBookingDlqProducerService failedUpdatedBookingDlqProducerService;

    @Spy
    private BookingMapper bookingMapper = new BookingMapperImpl();

    @Test
    void handleBookingCreationTest_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);
        booking.setPreviousCarId(1L);

        doNothing().when(updateBookingUpdateCarsProducerService).updateCarsStatus(any(UpdateCarsRequest.class));
        when(redisTemplate.delete(anyString())).thenReturn(true);
        doNothing().when(bookingProducerService).sendUpdatedBooking(any(BookingResponse.class));

        assertDoesNotThrow(() -> updatedBookingProcessorService.handleBookingUpdate(booking));
    }

    @Test
    void handleBookingCreationTest_failedOnUpdatingCar() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);

        doThrow(new RuntimeException("Test")).when(updateBookingUpdateCarsProducerService).updateCarsStatus(any(UpdateCarsRequest.class));
        doNothing().when(failedUpdatedBookingDlqProducerService).sendFailedUpdatedBooking(any(UpdatedBookingReprocessRequest.class));

        assertDoesNotThrow(() -> updatedBookingProcessorService.handleBookingUpdate(booking));
        verify(bookingMapper).mapToUpdatedBookingReprocessRequest(any(Booking.class));
    }

    @Test
    void handleBookingCreationTest_failedOnSendingBooking() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);
        booking.setPreviousCarId(1L);

        doNothing().when(updateBookingUpdateCarsProducerService).updateCarsStatus(any(UpdateCarsRequest.class));
        when(redisTemplate.delete(anyString())).thenReturn(true);
        doThrow(new RuntimeException("Test")).when(bookingProducerService).sendUpdatedBooking(any(BookingResponse.class));
        doNothing().when(failedUpdatedBookingDlqProducerService).sendFailedUpdatedBooking(any(UpdatedBookingReprocessRequest.class));

        assertDoesNotThrow(() -> updatedBookingProcessorService.handleBookingUpdate(booking));
        verify(bookingMapper).mapToUpdatedBookingReprocessRequest(any(Booking.class));
    }

}
