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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        when(carStatusUpdateProducerService.markCarAsAvailable(any(CarUpdateDetails.class))).thenReturn(true);
        when(bookingUpdateProducerService.closeBooking(any(BookingClosingDetails.class))).thenReturn(true);
        when(invoiceProducerService.sendMessage(any(InvoiceResponse.class))).thenReturn(true);
        doNothing().when(revenueService).addRevenue(any(Invoice.class));

        assertDoesNotThrow(() -> invoiceProcessorService.processInvoice(closedInvoice));
    }

    @Test
    void processInvoiceTest_failedCarUpdate() {
        Invoice closedInvoice = TestUtil.getResourceAsJson("/data/ClosedInvoice.json", Invoice.class);

        when(carStatusUpdateProducerService.markCarAsAvailable(any(CarUpdateDetails.class))).thenReturn(false);

        assertDoesNotThrow(() -> invoiceProcessorService.processInvoice(closedInvoice));

        verify(invoiceMapper).mapToInvoiceReprocessRequest(any(Invoice.class));
    }

    @Test
    void processInvoiceTest_failedBookingUpdate() {
        Invoice closedInvoice = TestUtil.getResourceAsJson("/data/ClosedInvoice.json", Invoice.class);

        when(carStatusUpdateProducerService.markCarAsAvailable(any(CarUpdateDetails.class))).thenReturn(true);
        when(bookingUpdateProducerService.closeBooking(any(BookingClosingDetails.class))).thenReturn(false);
        when(failedInvoiceDlqProducerService.sendMessage(any(InvoiceReprocessRequest.class))).thenReturn(true);

        assertDoesNotThrow(() -> invoiceProcessorService.processInvoice(closedInvoice));
    }

    @Test
    void processInvoiceTest_failedInvoiceSend() {
        Invoice closedInvoice = TestUtil.getResourceAsJson("/data/ClosedInvoice.json", Invoice.class);

        when(carStatusUpdateProducerService.markCarAsAvailable(any(CarUpdateDetails.class))).thenReturn(true);
        when(bookingUpdateProducerService.closeBooking(any(BookingClosingDetails.class))).thenReturn(true);
        when(invoiceProducerService.sendMessage(any(InvoiceResponse.class))).thenReturn(false);
        when(failedInvoiceDlqProducerService.sendMessage(any(InvoiceReprocessRequest.class))).thenReturn(true);

        assertDoesNotThrow(() -> invoiceProcessorService.processInvoice(closedInvoice));
    }

}
