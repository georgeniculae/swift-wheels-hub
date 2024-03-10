package com.swiftwheelshub.expense.service;

import com.swiftwheelshub.dto.BookingClosingDetails;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.dto.InvoiceRequest;
import com.swiftwheelshub.dto.InvoiceResponse;
import com.swiftwheelshub.entity.Invoice;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshub.expense.mapper.InvoiceMapper;
import com.swiftwheelshub.expense.mapper.InvoiceMapperImpl;
import com.swiftwheelshub.expense.repository.InvoiceRepository;
import com.swiftwheelshub.expense.util.AssertionUtils;
import com.swiftwheelshub.expense.util.TestUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @InjectMocks
    private InvoiceService invoiceService;

    @Mock
    private RevenueService revenueService;

    @Mock
    private BookingService bookingService;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Spy
    private InvoiceMapper invoiceMapper = new InvoiceMapperImpl();

    @Test
    void findAllInvoicesTest_success() {
        Invoice invoice = TestUtils.getResourceAsJson("/data/Invoice.json", Invoice.class);

        when(invoiceRepository.findAll()).thenReturn(List.of(invoice));

        assertDoesNotThrow(() -> invoiceService.findAllInvoices());
        List<InvoiceResponse> invoiceResponses = invoiceService.findAllInvoices();

        AssertionUtils.assertInvoiceResponse(invoice, invoiceResponses.getFirst());
    }

    @Test
    void findAllActiveInvoicesTest_success() {
        Invoice invoice = TestUtils.getResourceAsJson("/data/Invoice.json", Invoice.class);

        when(invoiceRepository.findAllActive()).thenReturn(List.of(invoice));

        assertDoesNotThrow(() -> invoiceService.findAllInvoices());
        List<InvoiceResponse> allActiveInvoices = invoiceService.findAllActiveInvoices();

        AssertionUtils.assertInvoiceResponse(invoice, allActiveInvoices.getFirst());
    }

    @Test
    void findInvoiceByIdTest_success() {
        Invoice invoice = TestUtils.getResourceAsJson("/data/Invoice.json", Invoice.class);

        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(invoice));

        assertDoesNotThrow(() -> invoiceService.findInvoiceById(1L));
        InvoiceResponse invoiceResponse = invoiceService.findInvoiceById(1L);

        AssertionUtils.assertInvoiceResponse(invoice, invoiceResponse);
    }

    @Test
    void findInvoiceByIdTest_errorOnFindingById() {
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.empty());

        SwiftWheelsHubNotFoundException swiftWheelsHubNotFoundException =
                assertThrows(SwiftWheelsHubNotFoundException.class, () -> invoiceService.findInvoiceById(1L));

        assertNotNull(swiftWheelsHubNotFoundException);
        assertEquals("Invoice with id 1 does not exist", swiftWheelsHubNotFoundException.getMessage());
    }

    @Test
    void findInvoiceByFilterTest_success() {
        Invoice invoice = TestUtils.getResourceAsJson("/data/Invoice.json", Invoice.class);

        when(invoiceRepository.findByComments(anyString())).thenReturn(List.of(invoice));

        List<InvoiceResponse> invoiceResponses =
                assertDoesNotThrow(() -> invoiceService.findInvoiceByComments("comment"));

        AssertionUtils.assertInvoiceResponse(invoice, invoiceResponses.getFirst());
    }

    @Test
    void findAllInvoicesByCustomerIdTest_success() {
        Invoice invoice = TestUtils.getResourceAsJson("/data/Invoice.json", Invoice.class);

        when(invoiceRepository.findByCustomerUsername(anyString())).thenReturn(List.of(invoice));

        List<InvoiceResponse> invoices = invoiceService.findAllInvoicesByCustomerUsername("user");

        AssertionUtils.assertInvoiceResponse(invoice, invoices.getFirst());
    }

    @Test
    void saveInvoice_success() {
        BookingResponse bookingResponse =
                TestUtils.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        Invoice invoice = TestUtils.getResourceAsJson("/data/Invoice.json", Invoice.class);

        when(invoiceRepository.existsByBookingId(anyLong())).thenReturn(false);
        when(invoiceRepository.saveAndFlush(any(Invoice.class))).thenReturn(invoice);

        assertDoesNotThrow(() -> invoiceService.saveInvoice(bookingResponse));
    }

    @Test
    void saveInvoice_error_existingInvoice() {
        BookingResponse bookingResponse =
                TestUtils.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        when(invoiceRepository.existsByBookingId(anyLong())).thenReturn(true);

        SwiftWheelsHubResponseStatusException exception =
                assertThrows(SwiftWheelsHubResponseStatusException.class, () -> invoiceService.saveInvoice(bookingResponse));

        assertThat(exception.getMessage()).contains("Invoice already exists");
    }

    @Test
    void updateInvoiceAfterBookingUpdateTest_success() {
        BookingResponse bookingResponse =
                TestUtils.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        Invoice invoice = TestUtils.getResourceAsJson("/data/Invoice.json", Invoice.class);

        when(invoiceRepository.findByBookingId(anyLong())).thenReturn(Optional.ofNullable(invoice));
        when(invoiceRepository.saveAndFlush(any(Invoice.class))).thenReturn(invoice);

        assertDoesNotThrow(() -> invoiceService.updateInvoiceAfterBookingUpdate(bookingResponse));
    }

    @Test
    void closeInvoiceTest_success() {
        Invoice invoice = TestUtils.getResourceAsJson("/data/Invoice.json", Invoice.class);

        InvoiceRequest invoiceRequest =
                TestUtils.getResourceAsJson("/data/InvoiceRequest.json", InvoiceRequest.class);

        BookingResponse bookingResponse =
                TestUtils.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-USERNAME", "user");

        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(invoice));
        when(bookingService.findBookingById(any(HttpServletRequest.class), anyLong())).thenReturn(bookingResponse);
        doNothing().when(revenueService).saveInvoiceAndRevenueTransactional(any(Invoice.class));
        when(invoiceRepository.saveAndFlush(any(Invoice.class))).thenReturn(invoice);
        doNothing().when(bookingService).closeBooking(any(HttpServletRequest.class), any(BookingClosingDetails.class));

        assertDoesNotThrow(() -> invoiceService.closeInvoice(request, 1L, invoiceRequest));

        verify(invoiceMapper, times(1)).mapEntityToDto(any(Invoice.class));
    }

    @Test
    void deleteInvoiceByBookingIdTest_success() {
        Invoice invoice = TestUtils.getResourceAsJson("/data/InProgressInvoice.json", Invoice.class);

        when(invoiceRepository.findByBookingId(anyLong())).thenReturn(Optional.ofNullable(invoice));

        assertDoesNotThrow(() -> invoiceService.deleteInvoiceByBookingId(1L));
    }

    @Test
    void deleteInvoiceByBookingIdTest_error_bookingInProgress() {
        Invoice invoice = TestUtils.getResourceAsJson("/data/Invoice.json", Invoice.class);

        when(invoiceRepository.findByBookingId(anyLong())).thenReturn(Optional.ofNullable(invoice));

        SwiftWheelsHubResponseStatusException exception =
                assertThrows(SwiftWheelsHubResponseStatusException.class, () -> invoiceService.deleteInvoiceByBookingId(1L));

        assertNotNull(exception);
        assertThat(exception.getMessage()).contains("Invoice cannot be deleted if booking is in progress");
    }

}
