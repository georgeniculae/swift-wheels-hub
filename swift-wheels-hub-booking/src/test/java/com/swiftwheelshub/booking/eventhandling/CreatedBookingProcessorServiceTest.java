package com.swiftwheelshub.booking.eventhandling;

import com.swiftwheelshub.booking.mapper.BookingMapper;
import com.swiftwheelshub.booking.mapper.BookingMapperImpl;
import com.swiftwheelshub.booking.producer.bookingprocessing.BookingProducerService;
import com.swiftwheelshub.booking.producer.bookingprocessing.CreatedBookingCarUpdateProducerService;
import com.swiftwheelshub.booking.producer.dlq.FailedCreatedBookingDlqProducerService;
import com.swiftwheelshub.booking.util.TestUtil;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.dto.CarStatusUpdate;
import com.swiftwheelshub.dto.CreatedBookingReprocessRequest;
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
class CreatedBookingProcessorServiceTest {

    @InjectMocks
    private CreatedBookingProcessorService createdBookingProcessorService;

    @Mock
    private CreatedBookingCarUpdateProducerService createdBookingCarUpdateProducerService;

    @Mock
    private BookingProducerService bookingProducerService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private FailedCreatedBookingDlqProducerService failedCreatedBookingDlqProducerService;

    @Spy
    private BookingMapper bookingMapper = new BookingMapperImpl();

    @Test
    void handleBookingCreationTest_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);

        doNothing().when(createdBookingCarUpdateProducerService).changeCarStatus(any(CarStatusUpdate.class));
        when(redisTemplate.delete(anyString())).thenReturn(true);
        doNothing().when(bookingProducerService).sendSavedBooking(any(BookingResponse.class));

        assertDoesNotThrow(() -> createdBookingProcessorService.handleBookingCreation(booking));
    }

    @Test
    void handleBookingCreationTest_failedOnUpdatingCar() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);

        doThrow(new RuntimeException("Test")).when(createdBookingCarUpdateProducerService).changeCarStatus(any(CarStatusUpdate.class));
        doNothing().when(failedCreatedBookingDlqProducerService).sendFailedCreatedBooking(any(CreatedBookingReprocessRequest.class));

        assertDoesNotThrow(() -> createdBookingProcessorService.handleBookingCreation(booking));
        verify(bookingMapper).mapToCreatedBookingReprocessRequest(any(Booking.class));
    }

    @Test
    void handleBookingCreationTest_failedOnSendingBooking() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);

        doNothing().when(createdBookingCarUpdateProducerService).changeCarStatus(any(CarStatusUpdate.class));
        when(redisTemplate.delete(anyString())).thenReturn(true);
        doThrow(new RuntimeException("Test")).when(bookingProducerService).sendSavedBooking(any(BookingResponse.class));
        doNothing().when(failedCreatedBookingDlqProducerService).sendFailedCreatedBooking(any(CreatedBookingReprocessRequest.class));

        assertDoesNotThrow(() -> createdBookingProcessorService.handleBookingCreation(booking));
        verify(bookingMapper).mapToCreatedBookingReprocessRequest(any(Booking.class));
    }

}
