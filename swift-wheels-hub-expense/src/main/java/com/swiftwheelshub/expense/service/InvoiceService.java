package com.swiftwheelshub.expense.service;

import com.swiftwheelshub.dto.BookingClosingDetails;
import com.swiftwheelshub.dto.BookingRequest;
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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class InvoiceService {

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

    public List<InvoiceResponse> findAllInvoicesByCustomerId(String customerUsername) {
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
        return invoiceRepository.findByComments(searchString)
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

    public InvoiceResponse saveInvoice(BookingResponse newBookingResponse) {
        if (!invoiceRepository.existsByBookingId(newBookingResponse.id())) {
            Invoice invoice = new Invoice();

            invoice.setCustomerUsername(newBookingResponse.customerUsername());
            invoice.setCustomerEmail(newBookingResponse.customerEmail());
            invoice.setCarId(newBookingResponse.carId());
            invoice.setBookingId(newBookingResponse.id());

            Invoice savedBooking = invoiceRepository.saveAndFlush(invoice);

            return invoiceMapper.mapEntityToDto(savedBooking);
        }

        throw new SwiftWheelsHubResponseStatusException(HttpStatus.BAD_REQUEST, "Invoice already exists");
    }

    public InvoiceResponse updateInvoiceAfterBookingUpdate(BookingResponse bookingResponse) {
        Invoice invoice = findInvoiceByBookingId(bookingResponse.id());
        invoice.setCarId(bookingResponse.carId());

        Invoice savedInvoice = invoiceRepository.saveAndFlush(invoice);

        return invoiceMapper.mapEntityToDto(savedInvoice);
    }

    public InvoiceResponse closeInvoice(HttpServletRequest request, Long id, InvoiceRequest invoiceRequest) {
        Invoice savedInvoice;
        BookingClosingDetails bookingClosingDetails;

        try {
            validateInvoice(invoiceRequest);
            Invoice existingInvoice = findEntityById(id);

            BookingRequest bookingRequest = bookingService.findBookingById(request, invoiceRequest.bookingId());
            Invoice existingInvoiceUpdated = updateInvoiceWithBookingDetails(bookingRequest, invoiceRequest, existingInvoice);

            revenueService.saveInvoiceAndRevenueTransactional(existingInvoiceUpdated);
            savedInvoice = invoiceRepository.saveAndFlush(existingInvoiceUpdated);

            bookingClosingDetails = getBookingClosingDetails(invoiceRequest, invoiceRequest.receptionistEmployeeId());
        } catch (Exception e) {
            throw new SwiftWheelsHubException(e);
        }

        bookingService.closeBooking(request, bookingClosingDetails);

        return invoiceMapper.mapEntityToDto(savedInvoice);
    }

    public void deleteInvoiceByBookingId(Long bookingId) {
        Invoice invoice = findInvoiceByBookingId(bookingId);

        if (ObjectUtils.isEmpty(invoice.getTotalAmount())) {
            invoiceRepository.deleteById(bookingId);

            return;
        }

        throw new SwiftWheelsHubResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invoice cannot be deleted if booking is in progress"
        );
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

    private double getDamageCost(InvoiceRequest invoiceRequest) {
        return ObjectUtils.isEmpty(invoiceRequest.damageCost()) ? 0D : invoiceRequest.damageCost();
    }

    private double getAdditionalPayment(InvoiceRequest invoiceRequest) {
        return ObjectUtils.isEmpty(invoiceRequest.additionalPayment()) ? 0D : invoiceRequest.additionalPayment();
    }

    private Invoice updateInvoiceWithBookingDetails(BookingRequest bookingRequest, InvoiceRequest invoiceRequest,
                                                    Invoice existingInvoice) {
        Long receptionistEmployeeId = invoiceRequest.receptionistEmployeeId();
        Long carId = invoiceRequest.carId();

        Invoice existingInvoiceUpdated =
                updateExistingInvoice(existingInvoice, invoiceRequest, carId, receptionistEmployeeId);

        return updateInvoiceAmount(bookingRequest, existingInvoiceUpdated);
    }

    private Invoice updateInvoiceAmount(BookingRequest bookingRequest, Invoice existingInvoice) {
        existingInvoice.setTotalAmount(getTotalAmount(existingInvoice, bookingRequest));

        return existingInvoice;
    }

    private BookingClosingDetails getBookingClosingDetails(InvoiceRequest invoiceRequest, Long receptionistEmployeeId) {
        return new BookingClosingDetails(
                invoiceRequest.bookingId(),
                receptionistEmployeeId,
                getCarStatus(invoiceRequest.isVehicleDamaged())
        );
    }

    private Double getTotalAmount(Invoice existingInvoice, BookingRequest bookingRequest) {
        LocalDate carReturnDate = existingInvoice.getCarDateOfReturn();
        LocalDate bookingDateTo = bookingRequest.dateTo();
        LocalDate bookingDateFrom = bookingRequest.dateFrom();
        double carAmount = bookingRequest.rentalCarPrice();

        boolean isReturnDatePassed = carReturnDate.isAfter(bookingDateTo);

        if (isReturnDatePassed) {
            return getAmountForLateReturn(carReturnDate, bookingDateTo, bookingDateFrom, carAmount);
        }

        return getDaysPeriod(bookingDateFrom, bookingDateTo) * carAmount +
                (ObjectUtils.isEmpty(existingInvoice.getDamageCost()) ? 0D : existingInvoice.getDamageCost());
    }

    private int getDaysPeriod(LocalDate bookingDateFrom, LocalDate bookingDateTo) {
        return Period.between(bookingDateFrom, bookingDateTo).getDays();
    }

    private double getAmountForLateReturn(LocalDate carReturnDate, LocalDate bookingDateTo, LocalDate bookingDateFrom,
                                          Double carAmount) {
        return getDaysPeriod(bookingDateFrom, bookingDateTo) * carAmount +
                getDaysPeriod(bookingDateTo, carReturnDate) * 2 * carAmount;
    }

    private CarState getCarStatus(boolean isVehicleDamaged) {
        return Boolean.TRUE.equals(isVehicleDamaged) ? CarState.BROKEN : CarState.AVAILABLE;
    }

}
