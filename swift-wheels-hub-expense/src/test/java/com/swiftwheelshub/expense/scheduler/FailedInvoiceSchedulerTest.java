package com.swiftwheelshub.expense.scheduler;

import com.swiftwheelshub.expense.repository.FailedBookingRollbackRepository;
import com.swiftwheelshub.expense.repository.InvoiceRepository;
import com.swiftwheelshub.expense.service.BookingService;
import com.swiftwheelshub.expense.service.CarService;
import com.swiftwheelshub.expense.service.RevenueService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ExtendWith(MockitoExtension.class)
class FailedInvoiceSchedulerTest {

    @InjectMocks
    private FailedInvoiceScheduler failedInvoiceScheduler;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private CarService carService;

    @Mock
    private BookingService bookingService;

    @Mock
    private RevenueService revenueService;

    @Mock
    private FailedBookingRollbackRepository failedBookingRollbackRepository;

    @Spy
    private ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

}
