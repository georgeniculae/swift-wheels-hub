package com.swiftwheelshub.expense.scheduler;

import com.swiftwheelshub.dto.AuthenticationInfo;
import com.swiftwheelshub.dto.BookingClosingDetails;
import com.swiftwheelshub.dto.BookingRollbackResponse;
import com.swiftwheelshub.dto.BookingUpdateResponse;
import com.swiftwheelshub.dto.CarUpdateDetails;
import com.swiftwheelshub.dto.StatusUpdateResponse;
import com.swiftwheelshub.entity.Invoice;
import com.swiftwheelshub.expense.model.FailedBookingRollback;
import com.swiftwheelshub.expense.repository.FailedBookingRollbackRepository;
import com.swiftwheelshub.expense.repository.InvoiceRepository;
import com.swiftwheelshub.expense.service.BookingService;
import com.swiftwheelshub.expense.service.CarService;
import com.swiftwheelshub.expense.service.RevenueService;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(failedInvoiceScheduler, "apikey", "apikey");
        ReflectionTestUtils.setField(failedInvoiceScheduler, "machineRole", "invoice_service");
    }

    @Test
    void processFailedInvoicesTest_success() throws InterruptedException {
        Invoice invoice = TestUtil.getResourceAsJson("/data/Invoice.json", Invoice.class);

        Invoice failedInvoice = TestUtil.getResourceAsJson("/data/FailedInvoice.json", Invoice.class);

        BookingUpdateResponse bookingUpdateResponse =
                TestUtil.getResourceAsJson("/data/SuccessfulBookingUpdateResponse.json", BookingUpdateResponse.class);

        StatusUpdateResponse statusUpdateResponse =
                TestUtil.getResourceAsJson("/data/SuccessfulStatusUpdateResponse.json", StatusUpdateResponse.class);

        when(invoiceRepository.findAllFailedInvoices()).thenReturn(List.of(failedInvoice));
        when(failedBookingRollbackRepository.doesNotExistByBookingId(anyLong())).thenReturn(true);
        when(bookingService.closeBooking(any(AuthenticationInfo.class), any(BookingClosingDetails.class)))
                .thenReturn(bookingUpdateResponse);
        when(carService.markCarAsAvailable(any(AuthenticationInfo.class), any(CarUpdateDetails.class)))
                .thenReturn(statusUpdateResponse);
        when(revenueService.processClosing(any(Invoice.class))).thenReturn(invoice);

        assertDoesNotThrow(() -> failedInvoiceScheduler.processFailedInvoices());

        verify(executorService).invokeAll(anyList());
    }

    @Test
    void processFailedInvoicesTest_failedCarUpdate_bookingRollbackSuccessful() {
        Invoice failedInvoice = TestUtil.getResourceAsJson("/data/FailedInvoice.json", Invoice.class);

        BookingUpdateResponse bookingUpdateResponse =
                TestUtil.getResourceAsJson("/data/SuccessfulBookingUpdateResponse.json", BookingUpdateResponse.class);

        StatusUpdateResponse statusUpdateResponse =
                TestUtil.getResourceAsJson("/data/FailedStatusUpdateResponse.json", StatusUpdateResponse.class);

        BookingRollbackResponse bookingRollbackResponse =
                TestUtil.getResourceAsJson("/data/SuccessfulBookingRollbackResponse.json", BookingRollbackResponse.class);

        when(invoiceRepository.findAllFailedInvoices()).thenReturn(List.of(failedInvoice));
        when(failedBookingRollbackRepository.doesNotExistByBookingId(anyLong())).thenReturn(true);
        when(bookingService.closeBooking(any(AuthenticationInfo.class), any(BookingClosingDetails.class)))
                .thenReturn(bookingUpdateResponse);
        when(carService.markCarAsAvailable(any(AuthenticationInfo.class), any(CarUpdateDetails.class)))
                .thenReturn(statusUpdateResponse);
        when(bookingService.rollbackBooking(any(AuthenticationInfo.class), anyLong()))
                .thenReturn(bookingRollbackResponse);

        assertDoesNotThrow(() -> failedInvoiceScheduler.processFailedInvoices());
    }

    @Test
    void processFailedInvoicesTest_failedCarUpdate_bookingRollbackFailed() {
        Invoice failedInvoice = TestUtil.getResourceAsJson("/data/FailedInvoice.json", Invoice.class);

        BookingUpdateResponse bookingUpdateResponse =
                TestUtil.getResourceAsJson("/data/SuccessfulBookingUpdateResponse.json", BookingUpdateResponse.class);

        StatusUpdateResponse statusUpdateResponse =
                TestUtil.getResourceAsJson("/data/FailedStatusUpdateResponse.json", StatusUpdateResponse.class);

        BookingRollbackResponse bookingRollbackResponse =
                TestUtil.getResourceAsJson("/data/FailedBookingRollbackResponse.json", BookingRollbackResponse.class);

        FailedBookingRollback failedBookingRollback =
                TestUtil.getResourceAsJson("/data/FailedBookingRollback.json", FailedBookingRollback.class);

        when(invoiceRepository.findAllFailedInvoices()).thenReturn(List.of(failedInvoice));
        when(failedBookingRollbackRepository.doesNotExistByBookingId(anyLong())).thenReturn(true);
        when(bookingService.closeBooking(any(AuthenticationInfo.class), any(BookingClosingDetails.class)))
                .thenReturn(bookingUpdateResponse);
        when(carService.markCarAsAvailable(any(AuthenticationInfo.class), any(CarUpdateDetails.class)))
                .thenReturn(statusUpdateResponse);
        when(bookingService.rollbackBooking(any(AuthenticationInfo.class), anyLong()))
                .thenReturn(bookingRollbackResponse);
        when(failedBookingRollbackRepository.save(any(FailedBookingRollback.class))).thenReturn(failedBookingRollback);

        assertDoesNotThrow(() -> failedInvoiceScheduler.processFailedInvoices());
    }

}
