package com.swiftwheelshub.expense.service;

import com.swiftwheelshub.dto.AuthenticationInfo;
import com.swiftwheelshub.dto.BookingClosingDetails;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.dto.BookingRollbackResponse;
import com.swiftwheelshub.dto.BookingUpdateResponse;
import com.swiftwheelshub.dto.CarState;
import com.swiftwheelshub.dto.CarUpdateDetails;
import com.swiftwheelshub.dto.InvoiceRequest;
import com.swiftwheelshub.dto.InvoiceResponse;
import com.swiftwheelshub.dto.StatusUpdateResponse;
import com.swiftwheelshub.entity.Invoice;
import com.swiftwheelshub.entity.InvoiceProcessStatus;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshub.expense.mapper.InvoiceMapper;
import com.swiftwheelshub.expense.model.FailedBookingRollback;
import com.swiftwheelshub.expense.repository.FailedBookingRollbackRepository;
import com.swiftwheelshub.expense.repository.InvoiceRepository;
import com.swiftwheelshub.lib.exceptionhandling.ExceptionUtil;
import com.swiftwheelshub.lib.util.AuthenticationUtil;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService implements RetryListener {

    private final InvoiceRepository invoiceRepository;
    private final RevenueService revenueService;
    private final BookingService bookingService;
    private final CarService carService;
    private final InvoiceMapper invoiceMapper;
    private final ExecutorService executorService;
    private final FailedBookingRollbackRepository failedBookingRollbackRepository;

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
        try {
            validateInvoice(invoiceRequest);

            AuthenticationInfo authenticationInfo = AuthenticationUtil.getAuthenticationInfo();
            Invoice existingInvoiceUpdated = updateInvoiceWithBookingDetails(id, authenticationInfo, invoiceRequest);

            Invoice savedInvoice = completeInvoiceAfterBookingAndCarUpdate(invoiceRequest, authenticationInfo, existingInvoiceUpdated);

            return invoiceMapper.mapEntityToDto(savedInvoice);
        } catch (Exception e) {
            log.error("Error occurred while closing invoice: {}", e.getMessage());

            throw ExceptionUtil.handleException(e);
        }
    }

    public void deleteInvoiceByBookingId(Long bookingId) {
        invoiceRepository.deleteByBookingId(bookingId);
    }

    private Invoice completeInvoiceAfterBookingAndCarUpdate(InvoiceRequest invoiceRequest,
                                                            AuthenticationInfo authenticationInfo,
                                                            Invoice existingInvoiceUpdated) {
        boolean successful = updateBookingAndCar(invoiceRequest, authenticationInfo, existingInvoiceUpdated);

        if (successful) {
            existingInvoiceUpdated.setInvoiceProcessStatus(InvoiceProcessStatus.SAVED_CLOSED_INVOICE);

            return revenueService.processClosing(existingInvoiceUpdated);
        }

        existingInvoiceUpdated.setInvoiceProcessStatus(InvoiceProcessStatus.FAILED_CLOSED_INVOICE);

        return invoiceRepository.save(existingInvoiceUpdated);
    }

    private boolean updateBookingAndCar(InvoiceRequest invoiceRequest,
                                        AuthenticationInfo authenticationInfo,
                                        Invoice savedInvoice) {
        BookingUpdateResponse bookingUpdateResponse = closeBooking(invoiceRequest, authenticationInfo);

        if (bookingUpdateResponse.isSuccessful()) {
            StatusUpdateResponse statusUpdateResponse = markCarAsAvailable(authenticationInfo, savedInvoice);

            if (statusUpdateResponse.isUpdateSuccessful()) {
                return true;
            } else {
                handleBookingRollback(authenticationInfo, savedInvoice);
            }
        }

        return false;
    }

    private StatusUpdateResponse markCarAsAvailable(AuthenticationInfo authenticationInfo, Invoice savedInvoice) {
        return carService.markCarAsAvailable(authenticationInfo, getCarUpdateDetails(savedInvoice));
    }

    private BookingUpdateResponse closeBooking(InvoiceRequest invoiceRequest, AuthenticationInfo authenticationInfo) {
        Long bookingId = invoiceRequest.bookingId();
        Long returnBranchId = invoiceRequest.returnBranchId();
        BookingClosingDetails bookingClosingDetails = getBookingClosingDetails(bookingId, returnBranchId);

        return bookingService.closeBooking(authenticationInfo, bookingClosingDetails);
    }

    private void handleBookingRollback(AuthenticationInfo authenticationInfo, Invoice savedInvoice) {
        Long bookingId = savedInvoice.getBookingId();

        BookingRollbackResponse rollbackBookingResponse =
                bookingService.rollbackBooking(authenticationInfo, bookingId);

        if (!rollbackBookingResponse.isSuccessful()) {
            failedBookingRollbackRepository.save(new FailedBookingRollback(bookingId));
        }
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

    private Invoice updateInvoiceWithBookingDetails(Long id,
                                                    AuthenticationInfo authenticationInfo,
                                                    InvoiceRequest invoiceRequest) {
        Invoice existingInvoice = getExistingInvoice(id);
        BookingResponse bookingResponse = getBookingResponse(authenticationInfo, invoiceRequest);

        Long receptionistEmployeeId = invoiceRequest.receptionistEmployeeId();
        Long carId = invoiceRequest.carId();
        String customerUsername = bookingResponse.customerUsername();

        existingInvoice.setCustomerUsername(customerUsername);
        existingInvoice.setCarReturnDate(invoiceRequest.carReturnDate());
        existingInvoice.setReceptionistEmployeeId(receptionistEmployeeId);
        existingInvoice.setCarId(carId);
        existingInvoice.setIsVehicleDamaged(invoiceRequest.isVehicleDamaged());
        existingInvoice.setDamageCost(getDamageCost(invoiceRequest));
        existingInvoice.setAdditionalPayment(getAdditionalPayment(invoiceRequest));
        existingInvoice.setComments(invoiceRequest.comments());
        existingInvoice.setTotalAmount(getTotalAmount(invoiceRequest, bookingResponse));
        existingInvoice.setInvoiceProcessStatus(InvoiceProcessStatus.IN_CLOSING);

        return invoiceRepository.save(existingInvoice);
    }

    private BookingResponse getBookingResponse(AuthenticationInfo authenticationInfo, InvoiceRequest invoiceRequest) {
        try {
            return executorService.submit(
                            () -> bookingService.findBookingById(
                                    authenticationInfo,
                                    invoiceRequest.bookingId()
                            )
                    )
                    .get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw ExceptionUtil.handleException(e);
        } catch (ExecutionException e) {
            throw ExceptionUtil.handleException(e);
        }
    }

    private Invoice getExistingInvoice(Long id) {
        try {
            return executorService.submit(() -> findEntityById(id)).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw ExceptionUtil.handleException(e);
        } catch (ExecutionException e) {
            throw ExceptionUtil.handleException(e);
        }
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

    private BigDecimal getTotalAmount(InvoiceRequest invoiceRequest, BookingResponse bookingResponse) {
        LocalDate carReturnDate = invoiceRequest.carReturnDate();
        LocalDate bookingDateTo = bookingResponse.dateTo();
        LocalDate bookingDateFrom = bookingResponse.dateFrom();
        BigDecimal carAmount = bookingResponse.rentalCarPrice();

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
