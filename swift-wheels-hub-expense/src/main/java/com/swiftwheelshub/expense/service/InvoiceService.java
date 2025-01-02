package com.swiftwheelshub.expense.service;

import com.swiftwheelshub.dto.BookingClosingDetails;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.dto.CarState;
import com.swiftwheelshub.dto.CarUpdateDetails;
import com.swiftwheelshub.dto.InvoiceReprocessRequest;
import com.swiftwheelshub.dto.InvoiceRequest;
import com.swiftwheelshub.dto.InvoiceResponse;
import com.swiftwheelshub.entity.Invoice;
import com.swiftwheelshub.entity.InvoiceProcessStatus;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshub.expense.mapper.InvoiceMapper;
import com.swiftwheelshub.expense.producer.BookingRollbackProducerService;
import com.swiftwheelshub.expense.producer.BookingUpdateProducerService;
import com.swiftwheelshub.expense.producer.CarStatusUpdateProducerService;
import com.swiftwheelshub.expense.producer.FailedInvoiceDlqProducerService;
import com.swiftwheelshub.expense.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService implements RetryListener {

    private final InvoiceRepository invoiceRepository;
    private final RevenueService revenueService;
    private final BookingUpdateProducerService bookingUpdateProducerService;
    private final CarStatusUpdateProducerService carStatusUpdateProducerService;
    private final InvoiceMapper invoiceMapper;
    private final FailedInvoiceDlqProducerService failedInvoiceDlqProducerService;
    private final BookingRollbackProducerService bookingRollbackProducerService;

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
            invoice.setRentalCarPrice(newBookingResponse.rentalCarPrice());
            invoice.setDateFrom(newBookingResponse.dateFrom());
            invoice.setDateTo(newBookingResponse.dateTo());

            invoiceRepository.save(invoice);

            return;
        }

        throw new SwiftWheelsHubResponseStatusException(HttpStatus.BAD_REQUEST, "Invoice already exists");
    }

    public void updateInvoiceAfterBookingUpdate(BookingResponse bookingResponse) {
        try {
            Invoice invoice = findInvoiceByBookingId(bookingResponse.id());
            invoice.setCarId(bookingResponse.carId());

            invoiceRepository.save(invoice);
        } catch (Exception e) {
            throw new SwiftWheelsHubResponseStatusException(HttpStatus.BAD_REQUEST, "Error while updating invoice");
        }
    }

    public void closeInvoice(Long id, InvoiceRequest invoiceRequest) {
        try {
            validateInvoice(invoiceRequest);
            Invoice existingInvoiceUpdated = updateInvoiceWithBookingDetails(id, invoiceRequest);

            completeInvoiceAfterBookingAndCarUpdate(invoiceRequest, existingInvoiceUpdated);
        } catch (Exception e) {
            log.error("Error occurred while closing invoice: {}", e.getMessage());

            failedInvoiceDlqProducerService.sendMessage(invoiceMapper.mapToInvoiceReprocessRequest(id, invoiceRequest));
        }
    }

    public void deleteInvoiceByBookingId(Long bookingId) {
        invoiceRepository.deleteByBookingId(bookingId);
    }

    private void completeInvoiceAfterBookingAndCarUpdate(InvoiceRequest invoiceRequest, Invoice existingInvoiceUpdated) {
        boolean successful = updateBookingAndCar(invoiceRequest, existingInvoiceUpdated);

        if (successful) {
            existingInvoiceUpdated.setInvoiceProcessStatus(InvoiceProcessStatus.SAVED_CLOSED_INVOICE);
            revenueService.processClosing(existingInvoiceUpdated);
            log.info("Invoice with id: {} has been successfully closed", existingInvoiceUpdated.getId());

            return;
        }

        existingInvoiceUpdated.setInvoiceProcessStatus(InvoiceProcessStatus.FAILED_CLOSED_INVOICE);
        invoiceRepository.save(existingInvoiceUpdated);

        InvoiceReprocessRequest invoiceReprocessRequest =
                invoiceMapper.mapToInvoiceReprocessRequest(existingInvoiceUpdated.getId(), invoiceRequest);

        failedInvoiceDlqProducerService.sendMessage(invoiceReprocessRequest);

        log.warn("Invoice with id: {} has failed to close, storing it to DLQ", existingInvoiceUpdated.getId());
    }

    private boolean updateBookingAndCar(InvoiceRequest invoiceRequest, Invoice savedInvoice) {
        boolean isBookingUpdated = closeBooking(invoiceRequest);

        if (isBookingUpdated) {
            boolean isCarUpdated = carStatusUpdateProducerService.markCarAsAvailable(getCarUpdateDetails(savedInvoice));

            if (isCarUpdated) {
                return true;
            } else {
                log.error("Error occurred while updating booking");
                bookingRollbackProducerService.rollbackBooking(savedInvoice.getBookingId());
            }
        }

        return false;
    }

    private boolean closeBooking(InvoiceRequest invoiceRequest) {
        Long bookingId = invoiceRequest.bookingId();
        Long returnBranchId = invoiceRequest.returnBranchId();
        BookingClosingDetails bookingClosingDetails = getBookingClosingDetails(bookingId, returnBranchId);

        return bookingUpdateProducerService.closeBooking(bookingClosingDetails);
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

    private Invoice updateInvoiceWithBookingDetails(Long id, InvoiceRequest invoiceRequest) {
        Invoice existingInvoice = findEntityById(id);

        Long receptionistEmployeeId = invoiceRequest.receptionistEmployeeId();
        Long carId = invoiceRequest.carId();

        existingInvoice.setCarReturnDate(invoiceRequest.carReturnDate());
        existingInvoice.setReceptionistEmployeeId(receptionistEmployeeId);
        existingInvoice.setCarId(carId);
        existingInvoice.setIsVehicleDamaged(invoiceRequest.isVehicleDamaged());
        existingInvoice.setDamageCost(getDamageCost(invoiceRequest));
        existingInvoice.setAdditionalPayment(getAdditionalPayment(invoiceRequest));
        existingInvoice.setComments(invoiceRequest.comments());
        existingInvoice.setTotalAmount(getTotalAmount(existingInvoice, invoiceRequest));
        existingInvoice.setReturnBranchId(invoiceRequest.returnBranchId());
        existingInvoice.setInvoiceProcessStatus(InvoiceProcessStatus.IN_CLOSING);

        return invoiceRepository.save(existingInvoice);
    }

    private CarUpdateDetails getCarUpdateDetails(Invoice invoice) {
        return CarUpdateDetails.builder()
                .carId(invoice.getCarId())
                .receptionistEmployeeId(invoice.getReceptionistEmployeeId())
                .carState(invoice.getIsVehicleDamaged() ? CarState.BROKEN : CarState.AVAILABLE)
                .build();
    }

    private BookingClosingDetails getBookingClosingDetails(Long bookingId, Long returnBranchId) {
        return BookingClosingDetails.builder()
                .bookingId(bookingId)
                .returnBranchId(returnBranchId)
                .build();
    }

    private BigDecimal getTotalAmount(Invoice existingInvoice, InvoiceRequest invoiceRequest) {
        LocalDate carReturnDate = invoiceRequest.carReturnDate();
        LocalDate bookingDateTo = existingInvoice.getDateTo();
        LocalDate bookingDateFrom = existingInvoice.getDateFrom();
        BigDecimal carAmount = existingInvoice.getRentalCarPrice();

        boolean isReturnDatePassed = carReturnDate.isAfter(bookingDateTo);

        if (isReturnDatePassed) {
            return getAmountForLateReturn(carReturnDate, bookingDateTo, bookingDateFrom, carAmount);
        }

        return carAmount.multiply(BigDecimal.valueOf(getDaysPeriod(bookingDateFrom, bookingDateTo)))
                .add(getDamageCost(invoiceRequest));
    }

    private int getDaysPeriod(LocalDate bookingDateFrom, LocalDate bookingDateTo) {
        return Period.between(bookingDateFrom, bookingDateTo).getDays();
    }

    private BigDecimal getAmountForLateReturn(LocalDate carReturnDate, LocalDate bookingDateTo, LocalDate
                                                      bookingDateFrom,
                                              BigDecimal carAmount) {
        return carAmount.multiply(BigDecimal.valueOf(getDaysPeriod(bookingDateFrom, bookingDateTo)))
                .add(BigDecimal.valueOf(getDaysPeriod(bookingDateTo, carReturnDate)).multiply(BigDecimal.valueOf(2)).multiply(carAmount));
    }

}
