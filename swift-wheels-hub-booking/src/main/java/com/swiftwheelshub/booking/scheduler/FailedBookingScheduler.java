package com.swiftwheelshub.booking.scheduler;

import com.swiftwheelshub.booking.repository.BookingRepository;
import com.swiftwheelshub.booking.service.CarService;
import com.swiftwheelshub.dto.AuthenticationInfo;
import com.swiftwheelshub.dto.CarState;
import com.swiftwheelshub.dto.CarUpdateDetails;
import com.swiftwheelshub.dto.StatusUpdateResponse;
import com.swiftwheelshub.dto.UpdateCarRequest;
import com.swiftwheelshub.entity.Booking;
import com.swiftwheelshub.entity.BookingProcessStatus;
import com.swiftwheelshub.entity.BookingStatus;
import com.swiftwheelshub.entity.CarStage;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
import com.swiftwheelshub.lib.exceptionhandling.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class FailedBookingScheduler {

    private final BookingRepository bookingRepository;
    private final CarService carService;
    private final ExecutorService executorService;

    @Value("${apikey.secret}")
    private String apikey;

    @Value("${apikey.machine-role}")
    private String machineRole;

    @Scheduled(fixedDelay = 5000)
    public void processFailedBookings() {
        try {
            List<Callable<StatusUpdateResponse>> callables = getCallables();
            List<Future<StatusUpdateResponse>> bookingFutures = executorService.invokeAll(callables);

            waitToComplete(bookingFutures);
        } catch (Exception e) {
            throw ExceptionUtil.handleException(e);
        } finally {
            shutdownExecutor();
        }
    }

    private List<Callable<StatusUpdateResponse>> getCallables() {
        return bookingRepository.findAllFailedBookings()
                .stream()
                .map(this::getCallable)
                .toList();
    }

    private Callable<StatusUpdateResponse> getCallable(Booking failedBooking) {
        return () -> {
            StatusUpdateResponse statusUpdateResponse = updateCar(failedBooking);

            if (statusUpdateResponse.isUpdateSuccessful()) {
                BookingProcessStatus bookingProcessStatus = getBookingProcessStatus(failedBooking.getBookingProcessStatus());
                failedBooking.setBookingProcessStatus(bookingProcessStatus);
                bookingRepository.save(failedBooking);
            }

            return null;
        };
    }

    private void waitToComplete(List<Future<StatusUpdateResponse>> futures) {
        for (Future<StatusUpdateResponse> future : futures) {
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

    private void shutdownExecutor() {
        executorService.shutdown();

        try {
            if (executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private StatusUpdateResponse updateCar(Booking failedBooking) {
        BookingStatus status = failedBooking.getStatus();
        Long actualCarId = failedBooking.getActualCarId();
        Long previousCarId = failedBooking.getPreviousCarId();

        if (ObjectUtils.isNotEmpty(previousCarId) && BookingStatus.IN_PROGRESS == status) {
            return carService.updateCarsStatuses(getAuthenticationInfo(), getCarsToUpdate(previousCarId, actualCarId));
        }

        if (BookingStatus.CLOSED == status) {
            return carService.updateCarWhenBookingIsFinished(getAuthenticationInfo(), getCarUpdateDetails(failedBooking));
        }

        return carService.changeCarStatus(getAuthenticationInfo(), actualCarId, CarState.NOT_AVAILABLE);
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

    private List<UpdateCarRequest> getCarsToUpdate(Long previousCarId, Long newCarId) {
        return List.of(
                new UpdateCarRequest(previousCarId, CarState.AVAILABLE),
                new UpdateCarRequest(newCarId, CarState.NOT_AVAILABLE)
        );
    }

    private CarUpdateDetails getCarUpdateDetails(Booking booking) {
        return CarUpdateDetails.builder()
                .carId(booking.getActualCarId())
                .receptionistEmployeeId(booking.getReturnBranchId())
                .carState(getCarPhase(booking.getCarStage()))
                .build();
    }

    private CarState getCarPhase(CarStage carStage) {
        return switch (carStage) {
            case AVAILABLE -> CarState.AVAILABLE;
            case BROKEN -> CarState.BROKEN;
        };
    }

    private BookingProcessStatus getBookingProcessStatus(BookingProcessStatus bookingProcessStatus) {
        return switch (bookingProcessStatus) {
            case FAILED_CREATED_BOOKING -> BookingProcessStatus.SAVED_CREATED_BOOKING;
            case FAILED_UPDATED_BOOKING -> BookingProcessStatus.SAVED_UPDATED_BOOKING;
            default -> BookingProcessStatus.SAVED_CLOSED_BOOKING;
        };
    }

}
