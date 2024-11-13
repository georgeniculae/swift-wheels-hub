package com.swiftwheelshub.expense.scheduler;

import com.swiftwheelshub.dto.AuthenticationInfo;
import com.swiftwheelshub.dto.BookingRollbackResponse;
import com.swiftwheelshub.expense.model.FailedBookingRollback;
import com.swiftwheelshub.expense.repository.FailedBookingRollbackRepository;
import com.swiftwheelshub.expense.service.BookingService;
import com.swiftwheelshub.expense.util.TestUtil;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(failedBookingRollbackScheduler, "apikey", "apikey");
        ReflectionTestUtils.setField(failedBookingRollbackScheduler, "machineRole", "invoice_service");
    }

    @Test
    void processFailedBookingRollbackTest_success() throws InterruptedException {
        FailedBookingRollback failedBookingRollback =
                TestUtil.getResourceAsJson("/data/FailedBookingRollback.json", FailedBookingRollback.class);

        BookingRollbackResponse bookingRollbackResponse =
                TestUtil.getResourceAsJson("/data/SuccessfulBookingRollbackResponse.json", BookingRollbackResponse.class);

        when(failedBookingRollbackRepository.findAll()).thenReturn(List.of(failedBookingRollback));
        when(bookingService.rollbackBooking(any(AuthenticationInfo.class), anyLong())).thenReturn(bookingRollbackResponse);
        doNothing().when(failedBookingRollbackRepository).deleteByBookingId(anyLong());

        failedBookingRollbackScheduler.processFailedBookingRollback();

        verify(failedBookingRollbackRepository).deleteByBookingId(anyLong());
        verify(executorService).invokeAll(anyList());
    }

}
