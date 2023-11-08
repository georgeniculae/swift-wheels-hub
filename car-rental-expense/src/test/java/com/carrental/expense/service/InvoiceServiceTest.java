package com.carrental.expense.service;

import com.carrental.dto.BookingClosingDetailsDto;
import com.carrental.dto.BookingDto;
import com.carrental.dto.InvoiceDto;
import com.carrental.entity.Invoice;
import com.carrental.expense.mapper.InvoiceMapper;
import com.carrental.expense.mapper.InvoiceMapperImpl;
import com.carrental.expense.repository.InvoiceRepository;
import com.carrental.expense.util.AssertionUtils;
import com.carrental.expense.util.TestUtils;
import com.carrental.lib.exception.CarRentalNotFoundException;
import com.carrental.lib.exception.CarRentalResponseStatusException;
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
    void saveInvoice_success() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);
        Invoice invoice = TestUtils.getResourceAsJson("/data/Invoice.json", Invoice.class);

        when(invoiceRepository.existsByBookingId(anyLong())).thenReturn(false);
        when(invoiceRepository.saveAndFlush(any(Invoice.class))).thenReturn(invoice);

        InvoiceDto invoiceDto = assertDoesNotThrow(() -> invoiceService.saveInvoice(bookingDto));
        assertNotNull(invoiceDto);
    }

    @Test
    void saveInvoice_error_existingInvoice() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        when(invoiceRepository.existsByBookingId(anyLong())).thenReturn(true);

        CarRentalResponseStatusException exception =
                assertThrows(CarRentalResponseStatusException.class, () -> invoiceService.saveInvoice(bookingDto));
        assertNotNull(exception);
        assertThat(exception.getMessage()).contains("Invoice already exists");
    }

    @Test
    void updateInvoiceAfterBookingUpdateTest_success() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);
        Invoice invoice = TestUtils.getResourceAsJson("/data/Invoice.json", Invoice.class);

        when(invoiceRepository.findByBookingId(anyLong())).thenReturn(Optional.ofNullable(invoice));
        when(invoiceRepository.saveAndFlush(any(Invoice.class))).thenReturn(invoice);

        InvoiceDto invoiceDto = assertDoesNotThrow(() -> invoiceService.updateInvoiceAfterBookingUpdate(bookingDto));
        assertNotNull(invoiceDto);
    }

    @Test
    void closeInvoiceTest_success() {
        Invoice invoice = TestUtils.getResourceAsJson("/data/Invoice.json", Invoice.class);
        InvoiceDto invoiceDto = TestUtils.getResourceAsJson("/data/InvoiceDto.json", InvoiceDto.class);
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-USERNAME", "user");

        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(invoice));
        when(bookingService.findBookingById(any(HttpServletRequest.class), anyLong())).thenReturn(bookingDto);
        doNothing().when(revenueService).saveInvoiceAndRevenueTransactional(any(Invoice.class));
        when(invoiceRepository.saveAndFlush(any(Invoice.class))).thenReturn(invoice);
        doNothing().when(bookingService).closeBooking(any(HttpServletRequest.class), any(BookingClosingDetailsDto.class));

        assertDoesNotThrow(() -> invoiceService.closeInvoice(request, 1L, invoiceDto));

        verify(invoiceMapper, times(1)).mapEntityToDto(any(Invoice.class));
    }

    @Test
    void findAllInvoicesTest_success() {
        Invoice invoice = TestUtils.getResourceAsJson("/data/Invoice.json", Invoice.class);

        when(invoiceRepository.findAll()).thenReturn(List.of(invoice));

        assertDoesNotThrow(() -> invoiceService.findAllInvoices());
        List<InvoiceDto> invoiceDtoList = invoiceService.findAllInvoices();

        AssertionUtils.assertInvoice(invoice, invoiceDtoList.get(0));
    }

    @Test
    void findAllActiveInvoicesTest_success() {
        Invoice invoice = TestUtils.getResourceAsJson("/data/Invoice.json", Invoice.class);

        when(invoiceRepository.findAllActive()).thenReturn(List.of(invoice));

        assertDoesNotThrow(() -> invoiceService.findAllInvoices());
        List<InvoiceDto> invoiceDtoList = invoiceService.findAllActiveInvoices();

        AssertionUtils.assertInvoice(invoice, invoiceDtoList.get(0));
    }

    @Test
    void findInvoiceByIdTest_success() {
        Invoice invoice = TestUtils.getResourceAsJson("/data/Invoice.json", Invoice.class);

        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(invoice));

        assertDoesNotThrow(() -> invoiceService.findInvoiceById(1L));
        InvoiceDto invoiceDto = invoiceService.findInvoiceById(1L);

        AssertionUtils.assertInvoice(invoice, invoiceDto);
    }

    @Test
    void findInvoiceByIdTest_errorOnFindingById() {
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.empty());

        CarRentalNotFoundException carRentalNotFoundException =
                assertThrows(CarRentalNotFoundException.class, () -> invoiceService.findInvoiceById(1L));

        assertNotNull(carRentalNotFoundException);
        assertEquals("Invoice with id 1 does not exist", carRentalNotFoundException.getMessage());
    }

    @Test
    void findInvoiceByFilterTest_success() {
        Invoice invoice = TestUtils.getResourceAsJson("/data/Invoice.json", Invoice.class);

        when(invoiceRepository.findByComments(anyString())).thenReturn(List.of(invoice));

        List<InvoiceDto> invoiceDtoList =
                assertDoesNotThrow(() -> invoiceService.findInvoiceByComments("comment"));

        AssertionUtils.assertInvoice(invoice, invoiceDtoList.get(0));
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

        CarRentalResponseStatusException exception =
                assertThrows(CarRentalResponseStatusException.class, () -> invoiceService.deleteInvoiceByBookingId(1L));

        assertNotNull(exception);
        assertThat(exception.getMessage()).contains("Invoice cannot be deleted if booking is in progress");
    }

}
