package com.swiftwheelshub.expense.service;

import com.swiftwheelshub.dto.BookingClosingDetails;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.dto.CarState;
import com.swiftwheelshub.dto.InvoiceRequest;
import com.swiftwheelshub.dto.InvoiceResponse;
import com.swiftwheelshub.entity.Invoice;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshub.expense.mapper.InvoiceMapper;
import com.swiftwheelshub.expense.repository.InvoiceRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class InvoiceService implements RetryListener {

    private final InvoiceRepository invoiceRepository;
    private final RevenueService revenueService;
    private final BookingService bookingService;
    private final InvoiceMapper invoiceMapper;

    public List<InvoiceResponse> findAllInvoices() {
        return invoiceRepository.findAll()
                .stream()
                .map(invoiceMapper::mapEntityToDto)
                .toList();
    }

    public List<InvoiceResponse> findAllActiveInvoices() {
        return invoiceRepository.findAllActive()
                .stream()
                .map(invoiceMapper::mapEntityToDto)
                .toList();
    }

    public List<InvoiceResponse> findAllInvoicesByCustomerUsername(String customerUsername) {
        return invoiceRepository.findByCustomerUsername(customerUsername)
                .stream()
                .map(invoiceMapper::mapEntityToDto)
                .toList();
    }

    public InvoiceResponse findInvoiceById(Long id) {
        Invoice invoice = findEntityById(id);

        return invoiceMapper.mapEntityToDto(invoice);
    }

    public List<InvoiceResponse> findInvoiceByComments(String searchString) {
        return invoiceRepository.findByCommentsIgnoreCase(searchString)
                .stream()
                .map(invoiceMapper::mapEntityToDto)
                .toList();
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

            invoiceRepository.saveAndFlush(invoice);

            return;
        }

        throw new SwiftWheelsHubResponseStatusException(HttpStatus.BAD_REQUEST, "Invoice already exists");
    }

    public void updateInvoiceAfterBookingUpdate(BookingResponse bookingResponse) {
        Invoice invoice = findInvoiceByBookingId(bookingResponse.id());
        invoice.setCarId(bookingResponse.carId());

        invoiceRepository.saveAndFlush(invoice);
    }

    public InvoiceResponse closeInvoice(HttpServletRequest request, Long id, InvoiceRequest invoiceRequest) {
        Invoice savedInvoice;
        BookingClosingDetails bookingClosingDetails;

        try {
            validateInvoice(invoiceRequest);
            Invoice existingInvoice = findEntityById(id);

            BookingResponse bookingResponse = bookingService.findBookingById(request, invoiceRequest.bookingId());
            Invoice existingInvoiceUpdated = updateInvoiceWithBookingDetails(bookingResponse, invoiceRequest, existingInvoice);

            revenueService.saveInvoiceAndRevenue(existingInvoiceUpdated);
            savedInvoice = invoiceRepository.saveAndFlush(existingInvoiceUpdated);

            bookingClosingDetails = getBookingClosingDetails(invoiceRequest, invoiceRequest.receptionistEmployeeId());
        } catch (Exception e) {
            throw new SwiftWheelsHubException(e);
        }

        bookingService.closeBooking(request, bookingClosingDetails);

        return invoiceMapper.mapEntityToDto(savedInvoice);
    }

    public void deleteInvoiceByBookingId(Long bookingId) {
        invoiceRepository.deleteByBookingId(bookingId);
    }

    private void validateInvoice(InvoiceRequest invoiceRequest) {
        validateDateOfReturnOfTheCar(Objects.requireNonNull(invoiceRequest.carDateOfReturn()));

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

    private Invoice updateExistingInvoice(Invoice existingInvoice, InvoiceRequest invoiceRequest, Long carId,
                                          Long receptionistEmployeeId) {
        existingInvoice.setCarDateOfReturn(invoiceRequest.carDateOfReturn());
        existingInvoice.setReceptionistEmployeeId(receptionistEmployeeId);
        existingInvoice.setCarId(carId);
        existingInvoice.setIsVehicleDamaged(invoiceRequest.isVehicleDamaged());
        existingInvoice.setDamageCost(getDamageCost(invoiceRequest));
        existingInvoice.setAdditionalPayment(getAdditionalPayment(invoiceRequest));
        existingInvoice.setComments(invoiceRequest.comments());

        return existingInvoice;
    }

    private BigDecimal getDamageCost(InvoiceRequest invoiceRequest) {
        return ObjectUtils.isEmpty(invoiceRequest.damageCost()) ? BigDecimal.ZERO : invoiceRequest.damageCost();
    }

    private BigDecimal getAdditionalPayment(InvoiceRequest invoiceRequest) {
        return ObjectUtils.isEmpty(invoiceRequest.additionalPayment()) ? BigDecimal.ZERO : invoiceRequest.additionalPayment();
    }

    private Invoice updateInvoiceWithBookingDetails(BookingResponse bookingResponse, InvoiceRequest invoiceRequest,
                                                    Invoice existingInvoice) {
        Long receptionistEmployeeId = invoiceRequest.receptionistEmployeeId();
        Long carId = invoiceRequest.carId();

        Invoice existingInvoiceUpdated =
                updateExistingInvoice(existingInvoice, invoiceRequest, carId, receptionistEmployeeId);

        return updateInvoiceAmount(bookingResponse, existingInvoiceUpdated);
    }

    private Invoice updateInvoiceAmount(BookingResponse bookingResponse, Invoice existingInvoice) {
        existingInvoice.setTotalAmount(getTotalAmount(existingInvoice, bookingResponse));

        return existingInvoice;
    }

    private BookingClosingDetails getBookingClosingDetails(InvoiceRequest invoiceRequest, Long receptionistEmployeeId) {
        return new BookingClosingDetails(
                invoiceRequest.bookingId(),
                receptionistEmployeeId,
                getCarStatus(invoiceRequest.isVehicleDamaged())
        );
    }

    private BigDecimal getTotalAmount(Invoice existingInvoice, BookingResponse bookingResponse) {
        LocalDate carReturnDate = existingInvoice.getCarDateOfReturn();
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
