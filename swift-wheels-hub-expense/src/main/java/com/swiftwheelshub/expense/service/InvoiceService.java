package com.swiftwheelshub.expense.service;

import com.swiftwheelshub.dto.BookingClosingDetails;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.dto.CarState;
import com.swiftwheelshub.dto.InvoiceRequest;
import com.swiftwheelshub.dto.InvoiceResponse;
import com.swiftwheelshub.entity.Invoice;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshub.expense.mapper.InvoiceMapper;
import com.swiftwheelshub.expense.repository.InvoiceRepository;
import com.swiftwheelshub.lib.exceptionhandling.ExceptionUtil;
import com.swiftwheelshub.lib.security.ApiKeyAuthenticationToken;
import com.swiftwheelshub.lib.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService implements RetryListener {

    private final InvoiceRepository invoiceRepository;
    private final RevenueService revenueService;
    private final BookingService bookingService;
    private final InvoiceMapper invoiceMapper;

    @Transactional(readOnly = true)
    public List<InvoiceResponse> findAllInvoices() {
        try (Stream<Invoice> invoiceStream = invoiceRepository.findAllInvoices()) {
            return invoiceStream.map(invoiceMapper::mapEntityToDto).toList();
        }
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> findAllActiveInvoices() {
        try (Stream<Invoice> invoiceStream = invoiceRepository.findAllActive()) {
            return invoiceStream.map(invoiceMapper::mapEntityToDto).toList();
        }
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> findAllInvoicesByCustomerUsername(String customerUsername) {
        try (Stream<Invoice> invoiceStream = invoiceRepository.findByCustomerUsername(customerUsername)) {
            return invoiceStream.map(invoiceMapper::mapEntityToDto).toList();
        }
    }

    public InvoiceResponse findInvoiceById(Long id) {
        Invoice invoice = findEntityById(id);

        return invoiceMapper.mapEntityToDto(invoice);
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> findInvoiceByComments(String searchString) {
        try (Stream<Invoice> invoiceStream = invoiceRepository.findByCommentsIgnoreCase(searchString)) {
            return invoiceStream.map(invoiceMapper::mapEntityToDto).toList();
        }
    }

    public Long countInvoices() {
        return invoiceRepository.count();
    }

    public long countAllActiveInvoices() {
        return invoiceRepository.countAllActive();
    }

    public void saveInvoice(BookingResponse newBookingResponse) {
        if (!invoiceRepository.existsByBookingId(newBookingResponse.id())) {
            Invoice invoice = new Invoice();

            invoice.setCustomerUsername(newBookingResponse.customerUsername());
            invoice.setCustomerEmail(newBookingResponse.customerEmail());
            invoice.setCarId(newBookingResponse.carId());
            invoice.setBookingId(newBookingResponse.id());

            invoiceRepository.save(invoice);

            return;
        }

        throw new SwiftWheelsHubResponseStatusException(HttpStatus.BAD_REQUEST, "Invoice already exists");
    }

    public void updateInvoiceAfterBookingUpdate(BookingResponse bookingResponse) {
        Invoice invoice = findInvoiceByBookingId(bookingResponse.id());
        invoice.setCarId(bookingResponse.carId());

        invoiceRepository.save(invoice);
    }

    public InvoiceResponse closeInvoice(Long id, InvoiceRequest invoiceRequest) {
        validateInvoice(invoiceRequest);

        Invoice savedInvoice;
        BookingClosingDetails bookingClosingDetails;

        ApiKeyAuthenticationToken principal = AuthenticationUtil.getAuthentication();
        String apikey = principal.getName();
        Collection<GrantedAuthority> authorities = principal.getAuthorities();

        try {
            Invoice existingInvoice = findEntityById(id);

            BookingResponse bookingResponse =
                    bookingService.findBookingById(apikey, authorities, invoiceRequest.bookingId());
            Invoice existingInvoiceUpdated =
                    updateInvoiceWithBookingDetails(bookingResponse, invoiceRequest, existingInvoice);

            revenueService.saveInvoiceAndRevenue(existingInvoiceUpdated);
            savedInvoice = invoiceRepository.save(existingInvoiceUpdated);

            bookingClosingDetails = getBookingClosingDetails(invoiceRequest, invoiceRequest.receptionistEmployeeId());
        } catch (Exception e) {
            log.error("Error occurred while closing invoice: {}", e.getMessage());

            throw ExceptionUtil.handleException(e);
        }

        bookingService.closeBooking(apikey, authorities, bookingClosingDetails);

        return invoiceMapper.mapEntityToDto(savedInvoice);
    }

    public void deleteInvoiceByBookingId(Long bookingId) {
        invoiceRepository.deleteByBookingId(bookingId);
    }

    private void validateInvoice(InvoiceRequest invoiceRequest) {
        validateDateOfReturnOfTheCar(invoiceRequest.carReturnDate());

        if (Boolean.TRUE.equals(invoiceRequest.isVehicleDamaged()) && ObjectUtils.isEmpty(invoiceRequest.damageCost())) {
            throw new SwiftWheelsHubResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "If the vehicle is damaged, the damage cost cannot be null/empty"
            );
        }
    }

    private Invoice findEntityById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new SwiftWheelsHubNotFoundException("Invoice with id " + id + " does not exist"));
    }

    private Invoice findInvoiceByBookingId(Long bookingId) {
        return invoiceRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new SwiftWheelsHubNotFoundException(
                                "Invoice with booking id: " + bookingId + " does not exist"
                        )
                );
    }

    private void validateDateOfReturnOfTheCar(LocalDate dateOfReturnOfTheCar) {
        LocalDate currentDate = LocalDate.now();

        if (dateOfReturnOfTheCar.isBefore(currentDate)) {
            throw new SwiftWheelsHubResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Date of return of the car cannot be in the past"
            );
        }
    }

    private BigDecimal getDamageCost(InvoiceRequest invoiceRequest) {
        return ObjectUtils.isEmpty(invoiceRequest.damageCost()) ? BigDecimal.ZERO : invoiceRequest.damageCost();
    }

    private BigDecimal getAdditionalPayment(InvoiceRequest invoiceRequest) {
        return ObjectUtils.isEmpty(invoiceRequest.additionalPayment()) ? BigDecimal.ZERO : invoiceRequest.additionalPayment();
    }

    private Invoice updateInvoiceWithBookingDetails(BookingResponse bookingResponse,
                                                    InvoiceRequest invoiceRequest,
                                                    Invoice existingInvoice) {
        Long receptionistEmployeeId = invoiceRequest.receptionistEmployeeId();
        Long carId = invoiceRequest.carId();
        String customerUsername = bookingResponse.customerUsername();
        String customerEmail = bookingResponse.customerEmail();

        Invoice updatedInvoice = invoiceMapper.getNewInvoiceInstance(existingInvoice);

        updatedInvoice.setCustomerUsername(customerUsername);
        updatedInvoice.setCustomerEmail(customerEmail);
        updatedInvoice.setCarReturnDate(invoiceRequest.carReturnDate());
        updatedInvoice.setReceptionistEmployeeId(receptionistEmployeeId);
        updatedInvoice.setCarId(carId);
        updatedInvoice.setIsVehicleDamaged(invoiceRequest.isVehicleDamaged());
        updatedInvoice.setDamageCost(getDamageCost(invoiceRequest));
        updatedInvoice.setAdditionalPayment(getAdditionalPayment(invoiceRequest));
        updatedInvoice.setComments(invoiceRequest.comments());
        updatedInvoice.setTotalAmount(getTotalAmount(existingInvoice, bookingResponse));

        return updatedInvoice;
    }

    private BookingClosingDetails getBookingClosingDetails(InvoiceRequest invoiceRequest, Long receptionistEmployeeId) {
        return new BookingClosingDetails(
                invoiceRequest.bookingId(),
                receptionistEmployeeId,
                getCarStatus(invoiceRequest.isVehicleDamaged())
        );
    }

    private BigDecimal getTotalAmount(Invoice existingInvoice, BookingResponse bookingResponse) {
        LocalDate carReturnDate = existingInvoice.getCarReturnDate();
        LocalDate bookingDateTo = bookingResponse.dateTo();
        LocalDate bookingDateFrom = bookingResponse.dateFrom();
        BigDecimal carAmount = bookingResponse.rentalCarPrice();

        boolean isReturnDatePassed = carReturnDate.isAfter(bookingDateTo);

        if (isReturnDatePassed) {
            return getAmountForLateReturn(carReturnDate, bookingDateTo, bookingDateFrom, carAmount);
        }

        return carAmount.multiply(BigDecimal.valueOf(getDaysPeriod(bookingDateFrom, bookingDateTo)))
                .add(ObjectUtils.isEmpty(existingInvoice.getDamageCost()) ? BigDecimal.ZERO : existingInvoice.getDamageCost());
    }

    private int getDaysPeriod(LocalDate bookingDateFrom, LocalDate bookingDateTo) {
        return Period.between(bookingDateFrom, bookingDateTo).getDays();
    }

    private BigDecimal getAmountForLateReturn(LocalDate carReturnDate, LocalDate bookingDateTo, LocalDate bookingDateFrom,
                                              BigDecimal carAmount) {
        return carAmount.multiply(BigDecimal.valueOf(getDaysPeriod(bookingDateFrom, bookingDateTo)))
                .add(BigDecimal.valueOf(getDaysPeriod(bookingDateTo, carReturnDate)).multiply(BigDecimal.valueOf(2)).multiply(carAmount));
    }

    private CarState getCarStatus(boolean isVehicleDamaged) {
        return Boolean.TRUE.equals(isVehicleDamaged) ? CarState.BROKEN : CarState.AVAILABLE;
    }

}
