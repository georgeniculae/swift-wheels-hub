package com.swiftwheelshub.booking.scheduler;

import com.swiftwheelshub.booking.repository.BookingRepository;
import com.swiftwheelshub.booking.service.CarService;
import com.swiftwheelshub.booking.util.TestUtil;
import com.swiftwheelshub.dto.AuthenticationInfo;
import com.swiftwheelshub.dto.CarState;
import com.swiftwheelshub.dto.CarUpdateDetails;
import com.swiftwheelshub.dto.StatusUpdateResponse;
import com.swiftwheelshub.entity.Booking;
import com.swiftwheelshub.entity.BookingProcessStatus;
import com.swiftwheelshub.entity.CarStage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FailedBookingSchedulerTest {

    @InjectMocks
    private FailedBookingScheduler failedBookingScheduler;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CarService carService;

    @Spy
    private ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(failedBookingScheduler, "apikey", "apikey");
        ReflectionTestUtils.setField(failedBookingScheduler, "machineRole", "booking_service");
    }

    @Test
    void processFailedBookingsTest_success_failedCreatedBooking() throws InterruptedException {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);
        booking.setBookingProcessStatus(BookingProcessStatus.FAILED_CREATED_BOOKING);

        StatusUpdateResponse statusUpdateResponse =
                TestUtil.getResourceAsJson("/data/SuccessfulStatusUpdateResponse.json", StatusUpdateResponse.class);

        when(bookingRepository.findAllFailedBookings()).thenReturn(List.of(booking));
        when(carService.changeCarStatus(any(AuthenticationInfo.class), anyLong(), any(CarState.class)))
                .thenReturn(statusUpdateResponse);

        assertDoesNotThrow(() -> failedBookingScheduler.processFailedBookings());

        verify(executorService).invokeAll(anyList());
    }

    @Test
    void processFailedBookingsTest_success_failedUpdatedBooking() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);
        booking.setPreviousCarId(1L);
        booking.setBookingProcessStatus(BookingProcessStatus.FAILED_UPDATED_BOOKING);

        StatusUpdateResponse statusUpdateResponse =
                TestUtil.getResourceAsJson("/data/SuccessfulStatusUpdateResponse.json", StatusUpdateResponse.class);

        when(bookingRepository.findAllFailedBookings()).thenReturn(List.of(booking));
        when(carService.updateCarsStatuses(any(AuthenticationInfo.class), anyList())).thenReturn(statusUpdateResponse);

        assertDoesNotThrow(() -> failedBookingScheduler.processFailedBookings());
    }

    @Test
    void processFailedBookingsTest_success_failedClosedBooking() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);
        booking.setBookingProcessStatus(BookingProcessStatus.FAILED_CLOSED_BOOKING);
        booking.setCarStage(CarStage.AVAILABLE);

        StatusUpdateResponse statusUpdateResponse =
                TestUtil.getResourceAsJson("/data/SuccessfulStatusUpdateResponse.json", StatusUpdateResponse.class);

        when(bookingRepository.findAllFailedBookings()).thenReturn(List.of(booking));
        when(carService.updateCarWhenBookingIsFinished(any(AuthenticationInfo.class), any(CarUpdateDetails.class)))
                .thenReturn(statusUpdateResponse);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        assertDoesNotThrow(() -> failedBookingScheduler.processFailedBookings());
    }

    @Test
    void processFailedBookingsTest_failedCarServiceCall() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);
        booking.setBookingProcessStatus(BookingProcessStatus.FAILED_CREATED_BOOKING);

        StatusUpdateResponse statusUpdateResponse =
                TestUtil.getResourceAsJson("/data/FailedStatusUpdateResponse.json", StatusUpdateResponse.class);

        when(bookingRepository.findAllFailedBookings()).thenReturn(List.of(booking));
        when(carService.changeCarStatus(any(AuthenticationInfo.class), anyLong(), any(CarState.class)))
                .thenReturn(statusUpdateResponse);

        assertDoesNotThrow(() -> failedBookingScheduler.processFailedBookings());
    }

}
