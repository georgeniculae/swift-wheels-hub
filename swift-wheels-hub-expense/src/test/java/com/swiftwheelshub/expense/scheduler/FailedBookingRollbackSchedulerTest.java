package com.swiftwheelshub.expense.scheduler;

import com.swiftwheelshub.expense.repository.FailedBookingRollbackRepository;
import com.swiftwheelshub.expense.service.BookingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ExtendWith(MockitoExtension.class)
class FailedBookingRollbackSchedulerTest {

    @InjectMocks
    private FailedBookingRollbackScheduler failedBookingRollbackScheduler;

    @Mock
    private FailedBookingRollbackRepository failedBookingRollbackRepository;

    @Mock
    private BookingService bookingService;

    @Spy
    private ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    @Test
    void processFailedBookingRollbackTest_success() {

    }

}
