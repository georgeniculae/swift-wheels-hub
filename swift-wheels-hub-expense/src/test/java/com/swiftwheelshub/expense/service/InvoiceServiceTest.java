package com.swiftwheelshub.expense.service;

import com.swiftwheelshub.dto.AuthenticationInfo;
import com.swiftwheelshub.dto.BookingClosingDetails;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.dto.BookingUpdateResponse;
import com.swiftwheelshub.dto.CarUpdateDetails;
import com.swiftwheelshub.dto.InvoiceRequest;
import com.swiftwheelshub.dto.InvoiceResponse;
import com.swiftwheelshub.dto.StatusUpdateResponse;
import com.swiftwheelshub.entity.Invoice;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshub.expense.mapper.InvoiceMapper;
import com.swiftwheelshub.expense.mapper.InvoiceMapperImpl;
import com.swiftwheelshub.expense.repository.InvoiceRepository;
import com.swiftwheelshub.expense.util.AssertionUtils;
import com.swiftwheelshub.expense.util.TestUtil;
import com.swiftwheelshub.lib.security.ApiKeyAuthenticationToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyString;
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

    @Mock
    private CarService carService;

    @Spy
    private ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    @Spy
    private InvoiceMapper invoiceMapper = new InvoiceMapperImpl();

    @Test
    void findAllInvoicesTest_success() {
        Invoice invoice = TestUtil.getResourceAsJson("/data/Invoice.json", Invoice.class);

        when(invoiceRepository.findAllInvoices()).thenReturn(Stream.of(invoice));

        List<InvoiceResponse> invoiceResponses = invoiceService.findAllInvoices();
        AssertionUtils.assertInvoiceResponse(invoice, invoiceResponses.getFirst());
    }

    @Test
    void findAllActiveInvoicesTest_success() {
        Invoice invoice = TestUtil.getResourceAsJson("/data/Invoice.json", Invoice.class);

        when(invoiceRepository.findAllActive()).thenReturn(Stream.of(invoice));

        assertDoesNotThrow(() -> invoiceService.findAllInvoices());
        List<InvoiceResponse> allActiveInvoices = invoiceService.findAllActiveInvoices();

        AssertionUtils.assertInvoiceResponse(invoice, allActiveInvoices.getFirst());
    }

    @Test
    void findInvoiceByIdTest_success() {
        Invoice invoice = TestUtil.getResourceAsJson("/data/Invoice.json", Invoice.class);

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
        assertEquals("Invoice with id 1 does not exist", swiftWheelsHubNotFoundException.getReason());
    }

    @Test
    void findInvoiceByFilterTest_success() {
        Invoice invoice = TestUtil.getResourceAsJson("/data/Invoice.json", Invoice.class);

        when(invoiceRepository.findByCommentsIgnoreCase(anyString())).thenReturn(Stream.of(invoice));

        List<InvoiceResponse> invoiceResponses =
                assertDoesNotThrow(() -> invoiceService.findInvoiceByComments("comment"));

        AssertionUtils.assertInvoiceResponse(invoice, invoiceResponses.getFirst());
    }

    @Test
    void findAllInvoicesByCustomerIdTest_success() {
        Invoice invoice = TestUtil.getResourceAsJson("/data/Invoice.json", Invoice.class);

        when(invoiceRepository.findByCustomerUsername(anyString())).thenReturn(Stream.of(invoice));

        List<InvoiceResponse> invoices = invoiceService.findAllInvoicesByCustomerUsername("user");

        AssertionUtils.assertInvoiceResponse(invoice, invoices.getFirst());
    }

    @Test
    void saveInvoice_success() {
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        Invoice invoice = TestUtil.getResourceAsJson("/data/Invoice.json", Invoice.class);

        when(invoiceRepository.existsByBookingId(anyLong())).thenReturn(false);
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

        assertDoesNotThrow(() -> invoiceService.saveInvoice(bookingResponse));
    }

    @Test
    void saveInvoice_error_existingInvoice() {
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        when(invoiceRepository.existsByBookingId(anyLong())).thenReturn(true);

        SwiftWheelsHubResponseStatusException exception =
                assertThrows(SwiftWheelsHubResponseStatusException.class, () -> invoiceService.saveInvoice(bookingResponse));

        assertThat(exception.getMessage()).contains("Invoice already exists");
    }

    @Test
    void updateInvoiceAfterBookingUpdateTest_success() {
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        Invoice invoice = TestUtil.getResourceAsJson("/data/Invoice.json", Invoice.class);

        when(invoiceRepository.findByBookingId(anyLong())).thenReturn(Optional.ofNullable(invoice));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

        assertDoesNotThrow(() -> invoiceService.updateInvoiceAfterBookingUpdate(bookingResponse));
    }

    @Test
    @SuppressWarnings("unchecked")
    void closeInvoiceTest_success() {
        Invoice invoice = TestUtil.getResourceAsJson("/data/Invoice.json", Invoice.class);

        Invoice closedInvoice = TestUtil.getResourceAsJson("/data/ClosedInvoice.json", Invoice.class);

        InvoiceRequest invoiceRequest =
                TestUtil.getResourceAsJson("/data/InvoiceRequest.json", InvoiceRequest.class);

        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        BookingUpdateResponse bookingUpdateResponse =
                TestUtil.getResourceAsJson("/data/BookingUpdateResponse.json", BookingUpdateResponse.class);

        StatusUpdateResponse statusUpdateResponse =
                TestUtil.getResourceAsJson("/data/StatusUpdateResponse.json", StatusUpdateResponse.class);

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.addHeader("X-API-KEY", "apikey");
        httpServletRequest.addHeader("X-ROLES", "ROLE_user");

        RequestAttributes servletWebRequest = new ServletWebRequest(httpServletRequest);
        RequestContextHolder.setRequestAttributes(servletWebRequest);

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");
        ApiKeyAuthenticationToken apiKeyAuthenticationToken =
                new ApiKeyAuthenticationToken(List.of(simpleGrantedAuthority), "apikey");

        SecurityContextHolder.getContext().setAuthentication(apiKeyAuthenticationToken);

        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(invoice));
        when(bookingService.findBookingById(any(AuthenticationInfo.class), anyLong())).thenReturn(bookingResponse);
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(closedInvoice);
        when(carService.markCarAsAvailable(any(AuthenticationInfo.class), any(CarUpdateDetails.class)))
                .thenReturn(statusUpdateResponse);
        when(bookingService.closeBooking(any(AuthenticationInfo.class), any(BookingClosingDetails.class)))
                .thenReturn(bookingUpdateResponse);
        when(revenueService.processClosing(any(Invoice.class))).thenReturn(invoice);

        InvoiceResponse invoiceResponse = invoiceService.closeInvoice(1L, invoiceRequest);
        AssertionUtils.assertInvoiceResponse(invoice, invoiceResponse);

        verify(invoiceMapper).mapEntityToDto(any(Invoice.class));
        verify(executorService, times(4)).submit(any(Callable.class));
    }

}
