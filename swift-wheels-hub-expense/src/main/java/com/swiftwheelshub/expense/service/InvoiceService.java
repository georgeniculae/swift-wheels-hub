package com.swiftwheelshub.expense.service;

import com.swiftwheelshub.dto.BookingClosingDetails;
import com.swiftwheelshub.dto.BookingRequest;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.dto.CarState;
import com.swiftwheelshub.dto.InvoiceDto;
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

    public List<InvoiceDto> findAllInvoices() {
        return invoiceRepository.findAll()
                .stream()
                .map(invoiceMapper::mapEntityToDto)
                .toList();
    }

    public List<InvoiceDto> findAllActiveInvoices() {
        return invoiceRepository.findAllActive()
                .stream()
                .map(invoiceMapper::mapEntityToDto)
                .toList();
    }

    public List<InvoiceDto> findAllInvoicesByCustomerId(String customerUsername) {
        return invoiceRepository.findByCustomerUsername(customerUsername)
                .stream()
                .map(invoiceMapper::mapEntityToDto)
                .toList();
    }

    public InvoiceDto findInvoiceById(Long id) {
        Invoice invoice = findEntityById(id);

        return invoiceMapper.mapEntityToDto(invoice);
    }

    public List<InvoiceDto> findInvoiceByComments(String searchString) {
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

    public InvoiceDto saveInvoice(BookingResponse newBookingResponse) {
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

    public InvoiceDto updateInvoiceAfterBookingUpdate(BookingResponse bookingResponse) {
        Invoice invoice = findInvoiceByBookingId(bookingResponse.id());
        invoice.setCarId(bookingResponse.carId());

        Invoice savedInvoice = invoiceRepository.saveAndFlush(invoice);

        return invoiceMapper.mapEntityToDto(savedInvoice);
    }

    public InvoiceDto closeInvoice(HttpServletRequest request, Long id, InvoiceDto invoiceDto) {
        Invoice savedInvoice;
        BookingClosingDetails bookingClosingDetails;

        try {
            validateInvoice(invoiceDto);
            Invoice existingInvoice = findEntityById(id);

            BookingRequest bookingRequest = bookingService.findBookingById(request, invoiceDto.bookingId());
            Invoice existingInvoiceUpdated = updateInvoiceWithBookingDetails(bookingRequest, invoiceDto, existingInvoice);

            revenueService.saveInvoiceAndRevenueTransactional(existingInvoiceUpdated);
            savedInvoice = invoiceRepository.saveAndFlush(existingInvoiceUpdated);

            bookingClosingDetails = getBookingClosingDetails(invoiceDto, invoiceDto.receptionistEmployeeId());
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

    private void validateInvoice(InvoiceDto invoiceDto) {
        validateDateOfReturnOfTheCar(Objects.requireNonNull(invoiceDto.carDateOfReturn()));

        if (Boolean.TRUE.equals(invoiceDto.isVehicleDamaged()) && ObjectUtils.isEmpty(invoiceDto.damageCost())) {
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

    private Invoice updateExistingInvoice(Invoice existingInvoice, InvoiceDto invoiceDto, Long carId,
                                          Long receptionistEmployeeId) {
        existingInvoice.setCarDateOfReturn(invoiceDto.carDateOfReturn());
        existingInvoice.setReceptionistEmployeeId(receptionistEmployeeId);
        existingInvoice.setCarId(carId);
        existingInvoice.setIsVehicleDamaged(invoiceDto.isVehicleDamaged());
        existingInvoice.setDamageCost(getDamageCost(invoiceDto));
        existingInvoice.setAdditionalPayment(getAdditionalPayment(invoiceDto));
        existingInvoice.setComments(invoiceDto.comments());

        return existingInvoice;
    }

    private double getDamageCost(InvoiceDto invoiceDto) {
        return ObjectUtils.isEmpty(invoiceDto.damageCost()) ? 0D : invoiceDto.damageCost();
    }

    private double getAdditionalPayment(InvoiceDto invoiceDto) {
        return ObjectUtils.isEmpty(invoiceDto.additionalPayment()) ? 0D : invoiceDto.additionalPayment();
    }

    private Invoice updateInvoiceWithBookingDetails(BookingRequest bookingRequest, InvoiceDto invoiceDto,
                                                    Invoice existingInvoice) {
        Long receptionistEmployeeId = invoiceDto.receptionistEmployeeId();
        Long carId = invoiceDto.carId();

        Invoice existingInvoiceUpdated =
                updateExistingInvoice(existingInvoice, invoiceDto, carId, receptionistEmployeeId);

        return updateInvoiceAmount(bookingRequest, existingInvoiceUpdated);
    }

    private Invoice updateInvoiceAmount(BookingRequest bookingRequest, Invoice existingInvoice) {
        existingInvoice.setTotalAmount(getTotalAmount(existingInvoice, bookingRequest));

        return existingInvoice;
    }

    private BookingClosingDetails getBookingClosingDetails(InvoiceDto invoiceDto, Long receptionistEmployeeId) {
        return new BookingClosingDetails(
                invoiceDto.bookingId(),
                receptionistEmployeeId,
                getCarStatus(invoiceDto.isVehicleDamaged())
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
