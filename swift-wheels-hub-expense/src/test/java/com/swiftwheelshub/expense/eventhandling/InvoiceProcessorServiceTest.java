package com.swiftwheelshub.expense.eventhandling;

import com.swiftwheelshub.dto.BookingClosingDetails;
import com.swiftwheelshub.dto.CarUpdateDetails;
import com.swiftwheelshub.dto.InvoiceReprocessRequest;
import com.swiftwheelshub.dto.InvoiceResponse;
import com.swiftwheelshub.entity.Invoice;
import com.swiftwheelshub.expense.mapper.InvoiceMapper;
import com.swiftwheelshub.expense.mapper.InvoiceMapperImpl;
import com.swiftwheelshub.expense.producer.BookingUpdateProducerService;
import com.swiftwheelshub.expense.producer.CarStatusUpdateProducerService;
import com.swiftwheelshub.expense.producer.FailedInvoiceDlqProducerService;
import com.swiftwheelshub.expense.producer.InvoiceProducerService;
import com.swiftwheelshub.expense.service.RevenueService;
import com.swiftwheelshub.expense.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InvoiceProcessorServiceTest {

    @InjectMocks
    private InvoiceProcessorService invoiceProcessorService;

    @Mock
    private RevenueService revenueService;

    @Mock
    private InvoiceProducerService invoiceProducerService;

    @Mock
    private BookingUpdateProducerService bookingUpdateProducerService;

    @Mock
    private CarStatusUpdateProducerService carStatusUpdateProducerService;

    @Mock
    private FailedInvoiceDlqProducerService failedInvoiceDlqProducerService;

    @Spy
    private InvoiceMapper invoiceMapper = new InvoiceMapperImpl();

    @Test
    void processInvoiceTest_success() {
        Invoice closedInvoice = TestUtil.getResourceAsJson("/data/ClosedInvoice.json", Invoice.class);

        doNothing().when(carStatusUpdateProducerService).markCarAsAvailable(any(CarUpdateDetails.class));
        doNothing().when(bookingUpdateProducerService).closeBooking(any(BookingClosingDetails.class));
        doNothing().when(invoiceProducerService).sendMessage(any(InvoiceResponse.class));
        doNothing().when(revenueService).addRevenue(any(Invoice.class));

        assertDoesNotThrow(() -> invoiceProcessorService.processInvoice(closedInvoice));
    }

    @Test
    void processInvoiceTest_failedCarUpdate() {
        Invoice closedInvoice = TestUtil.getResourceAsJson("/data/ClosedInvoice.json", Invoice.class);

        doThrow(new RuntimeException("Test")).when(carStatusUpdateProducerService).markCarAsAvailable(any(CarUpdateDetails.class));

        assertDoesNotThrow(() -> invoiceProcessorService.processInvoice(closedInvoice));

        verify(invoiceMapper).mapToInvoiceReprocessRequest(any(Invoice.class));
    }

    @Test
    void processInvoiceTest_failedBookingUpdate() {
        Invoice closedInvoice = TestUtil.getResourceAsJson("/data/ClosedInvoice.json", Invoice.class);

        doNothing().when(carStatusUpdateProducerService).markCarAsAvailable(any(CarUpdateDetails.class));
        doThrow(new RuntimeException("Test")).when(bookingUpdateProducerService).closeBooking(any(BookingClosingDetails.class));
        doNothing().when(failedInvoiceDlqProducerService).sendMessage(any(InvoiceReprocessRequest.class));

        assertDoesNotThrow(() -> invoiceProcessorService.processInvoice(closedInvoice));
    }

    @Test
    void processInvoiceTest_failedInvoiceSend() {
        Invoice closedInvoice = TestUtil.getResourceAsJson("/data/ClosedInvoice.json", Invoice.class);

        doNothing().when(carStatusUpdateProducerService).markCarAsAvailable(any(CarUpdateDetails.class));
        doNothing().when(bookingUpdateProducerService).closeBooking(any(BookingClosingDetails.class));
        doThrow(new RuntimeException("Test")).when(invoiceProducerService).sendMessage(any(InvoiceResponse.class));
        doNothing().when(failedInvoiceDlqProducerService).sendMessage(any(InvoiceReprocessRequest.class));

        assertDoesNotThrow(() -> invoiceProcessorService.processInvoice(closedInvoice));
    }

}
