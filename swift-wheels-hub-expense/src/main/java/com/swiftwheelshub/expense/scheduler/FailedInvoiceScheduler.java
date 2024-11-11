package com.swiftwheelshub.expense.scheduler;

import com.swiftwheelshub.dto.AuthenticationInfo;
import com.swiftwheelshub.dto.BookingClosingDetails;
import com.swiftwheelshub.dto.BookingUpdateResponse;
import com.swiftwheelshub.dto.CarState;
import com.swiftwheelshub.dto.CarUpdateDetails;
import com.swiftwheelshub.dto.StatusUpdateResponse;
import com.swiftwheelshub.entity.Invoice;
import com.swiftwheelshub.entity.InvoiceProcessStatus;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
import com.swiftwheelshub.expense.model.FailedBookingRollback;
import com.swiftwheelshub.expense.repository.FailedBookingRollbackRepository;
import com.swiftwheelshub.expense.repository.InvoiceRepository;
import com.swiftwheelshub.expense.service.BookingService;
import com.swiftwheelshub.expense.service.CarService;
import com.swiftwheelshub.expense.service.RevenueService;
import com.swiftwheelshub.lib.exceptionhandling.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Component
@RequiredArgsConstructor
@Slf4j
public class FailedInvoiceScheduler {

    private final InvoiceRepository invoiceRepository;
    private final CarService carService;
    private final BookingService bookingService;
    private final RevenueService revenueService;
    private final FailedBookingRollbackRepository failedBookingRollbackRepository;
    private final ExecutorService executorService;

    @Value("${apikey.secret}")
    private String apikey;

    @Value("${apikey.machine-role}")
    private String machineRole;

    @Scheduled(fixedDelay = 5000L)
    public void processFailedInvoices() {
        try {
            List<Callable<Object>> callables = getCallables();
            List<Future<Object>> futures = executorService.invokeAll(callables);

            waitToComplete(futures);
        } catch (Exception e) {
            throw ExceptionUtil.handleException(e);
        }
    }

    private List<Callable<Object>> getCallables() {
        return invoiceRepository.findAllFailedInvoices()
                .stream()
                .filter(this::shouldInvoiceBeProcessed)
                .map(this::getCallable)
                .toList();
    }

    private void waitToComplete(List<Future<Object>> futures) {
        for (Future<Object> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new SwiftWheelsHubException(e.getMessage());
            } catch (ExecutionException e) {
                throw new SwiftWheelsHubException(e.getMessage());
            }
        }
    }

    private Callable<Object> getCallable(Invoice failedInvoice) {
        return () -> {
            BookingUpdateResponse bookingUpdateResponse = closeBooking(failedInvoice);

            if (bookingUpdateResponse.isSuccessful()) {
                StatusUpdateResponse statusUpdateResponse = markCarAsAvailable(failedInvoice);

                handleFlowAfterBookingUpdate(failedInvoice, statusUpdateResponse);
            }

            return null;
        };
    }

    private void handleFlowAfterBookingUpdate(Invoice failedInvoice, StatusUpdateResponse statusUpdateResponse) {
        if (statusUpdateResponse.isUpdateSuccessful()) {
            failedInvoice.setInvoiceProcessStatus(InvoiceProcessStatus.SAVED_CLOSED_INVOICE);
            revenueService.processClosing(failedInvoice);

            return;
        }

        handleBookingRollback(getAuthenticationInfo(), failedInvoice);
    }

    private boolean shouldInvoiceBeProcessed(Invoice failedInvoice) {
        return failedBookingRollbackRepository.doesNotExistByBookingId(failedInvoice.getBookingId());
    }

    private BookingUpdateResponse closeBooking(Invoice failedInvoice) {
        Long bookingId = failedInvoice.getBookingId();
        Long receptionistEmployeeId = failedInvoice.getReceptionistEmployeeId();
        BookingClosingDetails bookingClosingDetails = getBookingClosingDetails(bookingId, receptionistEmployeeId);

        return bookingService.closeBooking(getAuthenticationInfo(), bookingClosingDetails);
    }

    private StatusUpdateResponse markCarAsAvailable(Invoice failedInvoice) {
        return carService.markCarAsAvailable(getAuthenticationInfo(), getCarUpdateDetails(failedInvoice));
    }

    private AuthenticationInfo getAuthenticationInfo() {
        return AuthenticationInfo.builder()
                .apikey(apikey)
                .roles(getRoles())
                .build();
    }

    private List<String> getRoles() {
        return List.of(machineRole);
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

    private void handleBookingRollback(AuthenticationInfo authenticationInfo, Invoice savedInvoice) {
        Long bookingId = savedInvoice.getBookingId();

        BookingUpdateResponse rollbackBookingResponse =
                bookingService.rollbackBooking(authenticationInfo, bookingId);

        if (rollbackBookingResponse.isSuccessful()) {
            failedBookingRollbackRepository.save(new FailedBookingRollback(bookingId));
        }
    }

}
