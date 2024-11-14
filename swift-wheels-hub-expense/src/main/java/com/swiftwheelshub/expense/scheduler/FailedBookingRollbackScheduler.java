package com.swiftwheelshub.expense.scheduler;

import com.swiftwheelshub.dto.AuthenticationInfo;
import com.swiftwheelshub.dto.BookingRollbackResponse;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
import com.swiftwheelshub.expense.model.FailedBookingRollback;
import com.swiftwheelshub.expense.repository.FailedBookingRollbackRepository;
import com.swiftwheelshub.expense.service.BookingService;
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
public class FailedBookingRollbackScheduler {

    private final FailedBookingRollbackRepository failedBookingRollbackRepository;
    private final BookingService bookingService;
    private final ExecutorService executorService;

    @Value("${apikey.secret}")
    private String apikey;

    @Value("${apikey.machine-role}")
    private String machineRole;

    @Scheduled(fixedDelay = 5000L)
    public void processFailedBookingRollback() {
        try {
            List<Callable<BookingRollbackResponse>> callables = getCallables();

            executorService.invokeAll(callables)
                    .stream()
                    .map(this::getBookingRollbackResponse)
                    .filter(BookingRollbackResponse::isSuccessful)
                    .forEach(this::deleteByBookingId);
        } catch (Exception e) {
            throw ExceptionUtil.handleException(e);
        }
    }

    private BookingRollbackResponse getBookingRollbackResponse(Future<BookingRollbackResponse> bookingRollbackResponseFuture) {
        try {
            return bookingRollbackResponseFuture.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SwiftWheelsHubException(e.getMessage());
        } catch (ExecutionException e) {
            throw new SwiftWheelsHubException(e.getMessage());
        }
    }

    private List<Callable<BookingRollbackResponse>> getCallables() {
        return failedBookingRollbackRepository.findAll()
                .stream()
                .map(this::getBookingRollbackResponseCallable)
                .toList();
    }

    private Callable<BookingRollbackResponse> getBookingRollbackResponseCallable(FailedBookingRollback failedBookingRollback) {
        return () -> bookingService.rollbackBooking(getAuthenticationInfo(), failedBookingRollback.getBookingId());
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

    private void deleteByBookingId(BookingRollbackResponse bookingRollbackResponse) {
        failedBookingRollbackRepository.deleteByBookingId(bookingRollbackResponse.bookingId());
    }

}
